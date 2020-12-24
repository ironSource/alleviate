package com.rotemati.foregroundsdk.foregroundtask.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandler
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandlerImpl
import com.rotemati.foregroundsdk.connectivity.ConnectivityJobService
import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import com.rotemati.foregroundsdk.notification.DefaultNotificationDescriptorCreator
import com.rotemati.foregroundsdk.notification.NotificationBuilder
import com.rotemati.foregroundsdk.notification.NotificationChannelsCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor

private const val JOB_ID_NOT_VALID: Int = -1
private const val NOTIFICATION_ID: Int = 654321

abstract class BaseForegroundTaskService : Service() {

	private lateinit var foregroundTasksScheduler: ForegroundTasksScheduler
	private lateinit var mConnectivityHandler: ConnectivityHandler
	private lateinit var pendingTasksRepository: PendingTasksRepository
	private lateinit var defaultNotificationDescriptorCreator: DefaultNotificationDescriptorCreator
	private lateinit var notificationBuilder: NotificationBuilder
	private lateinit var foregroundTaskInfo: ForegroundTaskInfo

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	abstract fun startWork(): Result

	abstract fun getNotification(): Notification

	fun getForegroundTaskInfo() = foregroundTaskInfo

	override fun onCreate() {
		super.onCreate()
		foregroundTasksScheduler = ForegroundTasksScheduler(this)
		mConnectivityHandler = ConnectivityHandlerImpl()
		mConnectivityHandler.register(this)
		pendingTasksRepository = PendingTasksRepository(this)
		defaultNotificationDescriptorCreator = DefaultNotificationDescriptorCreator()
		notificationBuilder = NotificationBuilder(this, NotificationChannelsCreator(this))
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		if (intent == null) {
			return START_NOT_STICKY
		}
		val jobId = intent.getIntExtra(EXTRA_TASK_ID, JOB_ID_NOT_VALID)

		if (jobId == JOB_ID_NOT_VALID) {
			onError("job id not valid")
			return START_NOT_STICKY
		}
		if (!pendingTasksRepository.contains(jobId)) {
			onError("job isn't in the repo")
			return START_NOT_STICKY
		}
		foregroundTaskInfo = pendingTasksRepository.foregroundTasks.single { it.id == jobId }

		if (!isConnectionAllowed(foregroundTaskInfo.networkType)) {
			onError("Connection type isn't allowed")
			logger.i("Scheduling connectivity job service")
			ConnectivityJobService.schedule(this, javaClass, foregroundTaskInfo)
			return START_NOT_STICKY
		}

		val foregroundNotification = getNotification()
		logger.d("Showing foreground")
		startForeground(NOTIFICATION_ID, foregroundNotification)

		when (val result = startWork()) {
			is Result.Success -> onFinished()
			is Result.Failed -> onFailed(result.exception)
			is Result.Reschedule -> onReschedule()
		}

		return START_NOT_STICKY
	}

	private fun onReschedule() {
		foregroundTaskInfo = ForegroundTaskInfo(
				id = foregroundTaskInfo.id,
				networkType = foregroundTaskInfo.networkType,
				persisted = foregroundTaskInfo.persisted,
				minLatencyMillis = foregroundTaskInfo.minLatencyMillis,
				timeoutMillis = foregroundTaskInfo.timeoutMillis,
				retryCount = foregroundTaskInfo.retryCount + 1
		)
		foregroundTasksScheduler.scheduleForeground(javaClass, foregroundTaskInfo)
		logger.d("Stopping foreground")
		stopForeground(true)
		stopSelf()
	}

	private fun onFailed(exception: Exception) {
		exception.message?.let { logger.e(it) }
		pendingTasksRepository.remove(foregroundTaskInfo.id)
		logger.d("Stopping foreground")
		stopForeground(true)
		stopSelf()
	}

	private fun onFinished() {
		logger.i("Task completed successfully - removing it from repo")
		foregroundTaskInfo.id.let { pendingTasksRepository.remove(it) }
		logger.d("Stopping foreground")
		stopForeground(true)
		stopSelf()
	}

	private fun onError(
			error: String,
			notificationDescriptor: NotificationDescriptor = defaultNotificationDescriptorCreator.create()
	) {
		logger.e(error)
		startForeground(NOTIFICATION_ID, notificationBuilder.build(notificationDescriptor))
		stopForeground(true)
		stopSelf()
	}

	private fun isConnectionAllowed(networkType: NetworkType): Boolean {
		if (networkType == NetworkType.None) {
			logger.i("Task doesn't require any network")
			return true
		} else {
			if (networkType == NetworkType.NotRoaming && mConnectivityHandler.isRoaming(this)) {
				logger.i("Task requires not roaming but in roaming")
				return false
			} else {
				if (mConnectivityHandler.isBlocked) {
					logger.i("Task requires network but network is blocked")
					return false
				}
				if (!mConnectivityHandler.isConnected(this)) {
					logger.i("Task Requires network but no internet connection")
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