package com.rotemati.foregroundsdk.foregroundtask

import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandler
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandlerImpl
import com.rotemati.foregroundsdk.connectivity.ConnectivityJobService
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundsdk.logger.SDKLogger
import com.rotemati.foregroundsdk.notification.DefaultNotificationDescriptorCreator
import com.rotemati.foregroundsdk.notification.NotificationBuilder
import com.rotemati.foregroundsdk.notification.NotificationChannelsCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor
import com.rotemati.foregroundsdk.reschedule.EligibleForRescheduling

private const val JOB_ID_NOT_VALID: Int = -1
private const val NOTIFICATION_ID: Int = 654321

abstract class BaseForegroundTaskService : Service() {

	private lateinit var foregroundTasksScheduler: ForegroundTasksScheduler
	private lateinit var mConnectivityHandler: ConnectivityHandler
	private lateinit var pendingTasksRepository: PendingTasksRepository
	private lateinit var defaultNotificationDescriptorCreator: DefaultNotificationDescriptorCreator
	private lateinit var notificationBuilder: NotificationBuilder
	private lateinit var eligibleForRescheduling: EligibleForRescheduling
	private lateinit var foregroundTaskInfo: ForegroundTaskInfo

	abstract fun startWork(): Result

	fun getForegroundTaskInfo() = foregroundTaskInfo

	override fun onCreate() {
		super.onCreate()
		foregroundTasksScheduler = ForegroundTasksScheduler(this)
		mConnectivityHandler = ConnectivityHandlerImpl()
		mConnectivityHandler.register(this)
		pendingTasksRepository = PendingTasksRepository(this)
		eligibleForRescheduling = EligibleForRescheduling()
		defaultNotificationDescriptorCreator = DefaultNotificationDescriptorCreator()
		notificationBuilder = NotificationBuilder(this, NotificationChannelsCreator(this))
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		SDKLogger.logMethod()
		if (intent == null) {
			return START_NOT_STICKY
		}
		val jobId = intent.getIntExtra(EXTRA_TASK_ID, JOB_ID_NOT_VALID)

		if (jobId == JOB_ID_NOT_VALID) {
			onError("job id not valid")
			return START_NOT_STICKY
		}
		if (!pendingTasksRepository.pendingForegroundTasks.contains(jobId)) {
			onError("job isn't in the repo")
			return START_NOT_STICKY
		}
		foregroundTaskInfo = pendingTasksRepository.pendingForegroundTasks.single { it.id == jobId }

		if (!isConnectionAllowed(foregroundTaskInfo.networkType)) {
			onError("Connection type isn't allowed")
			SDKLogger.i("Scheduling connectivity job service")
			ConnectivityJobService.schedule(
                    this,
                    javaClass,
                    foregroundTaskInfo.persisted,
                    foregroundTaskInfo.networkType,
                    foregroundTaskInfo.id
            )
			return START_NOT_STICKY
		}

		startForeground(NOTIFICATION_ID, foregroundTaskInfo.notification)

		val result = startWork()
		when (result) {
            is Result.Success -> onFinished()
            is Result.Failed -> onFailed(result.exception)
            is Result.Reschedule -> foregroundTasksScheduler.scheduleForeground(
                    javaClass,
                    foregroundTaskInfo
            )
		}

		return START_NOT_STICKY
	}

	fun onFailed(exception: Exception) {
		exception.message?.let { SDKLogger.e(it) }
		val newJobInfo = foregroundTaskInfo {
			id = foregroundTaskInfo.id
			networkType = foregroundTaskInfo.networkType
			persisted = foregroundTaskInfo.persisted
			minLatencyMillis = foregroundTaskInfo.minLatencyMillis
			timeoutMillis = foregroundTaskInfo.timeoutMillis
			notification = foregroundTaskInfo.notification
			retryPolicy = foregroundTaskInfo.retryPolicy
			retryCount = foregroundTaskInfo.retryCount + 1
		}
		if (eligibleForRescheduling.isEligible(newJobInfo)) {
			foregroundTasksScheduler.scheduleForeground(javaClass, foregroundTaskInfo)
		} else {
			SDKLogger.i("max retries reached - removing it from repo")
			pendingTasksRepository.remove(foregroundTaskInfo.id)
		}
		stopForeground(true)
		stopSelf()
	}

	fun onFinished() {
		SDKLogger.i("Task completed successfully - removing it from repo")
		foregroundTaskInfo.id.let { pendingTasksRepository.remove(it) }
		stopForeground(true)
		stopSelf()
	}

	private fun onError(
            error: String,
            notificationDescriptor: NotificationDescriptor = defaultNotificationDescriptorCreator.create()
    ) {
		SDKLogger.e(error)
		startForeground(NOTIFICATION_ID, notificationBuilder.build(notificationDescriptor))
		stopForeground(true)
		stopSelf()
	}

	private fun isConnectionAllowed(networkType: NetworkType): Boolean {
		if (networkType == NetworkType.None) {
			SDKLogger.i("Task doesn't require any network")
			return true
		} else {
			if (networkType == NetworkType.NotRoaming && mConnectivityHandler.isRoaming(this)) {
				SDKLogger.i("Task requires not roaming but in roaming")
				return false
			} else {
				if (mConnectivityHandler.isBlocked) {
					SDKLogger.i("Task requires network but network is blocked")
					return false
				}
				if (!mConnectivityHandler.isConnected(this)) {
					SDKLogger.i("Task Requires network but no internet connection")
					return false
				}
			}
			return true
		}
	}

	override fun onBind(intent: Intent?): Nothing? = null

	companion object {
		const val EXTRA_TASK_ID = "com.ironsource.foreground.EXTRA_TASK_ID"
	}
}