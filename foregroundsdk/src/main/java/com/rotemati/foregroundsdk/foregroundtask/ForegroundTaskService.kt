package com.rotemati.foregroundsdk.foregroundtask

import android.app.Service
import android.app.job.JobInfo
import android.content.Intent
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandler
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandlerImpl
import com.rotemati.foregroundsdk.connectivity.ConnectivityJobService
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.logger.SDKLogger
import com.rotemati.foregroundsdk.notification.DefaultNotificationDescriptorCreator
import com.rotemati.foregroundsdk.notification.NotificationBuilder
import com.rotemati.foregroundsdk.notification.NotificationChannelsCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor
import com.rotemati.foregroundsdk.reschedule.EligibleForRescheduling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

private const val NOTIFICATION_ID: Int = 654321
private const val JOB_ID_NOT_VALID: Int = -1

internal class ForegroundTaskService : Service() {

	private lateinit var foregroundTasksScheduler: ForegroundTasksScheduler
	private lateinit var mConnectivityHandler: ConnectivityHandler
	private lateinit var pendingTasksRepository: PendingTasksRepository
	private lateinit var eligibleForRescheduling: EligibleForRescheduling
	private lateinit var defaultNotificationDescriptorCreator: DefaultNotificationDescriptorCreator
	private lateinit var notificationBuilder: NotificationBuilder

	override fun onCreate() {
		super.onCreate()
		foregroundTasksScheduler = ForegroundTasksScheduler()
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
		intent?.let { nonNullIntent ->
			val jobId = nonNullIntent.getIntExtra(EXTRA_TASK_ID, JOB_ID_NOT_VALID)

			if (jobId == JOB_ID_NOT_VALID) {
				onError("job id not valid")
				return START_NOT_STICKY
			}
			val jobInfo = pendingTasksRepository.pendingForegroundTasks.find { it.id == jobId }

			if (jobInfo == null) {
				onError("job isn't in the repo")
				return START_NOT_STICKY
			}

			if (!isConnectionAllowed(jobInfo.networkType)) {
				onError("Connection type isn't allowed")
				SDKLogger.i("Scheduling connectivity job service")
				ConnectivityJobService.schedule(this, jobInfo.persisted, jobInfo.networkType, jobInfo.id)
				return START_NOT_STICKY
			}

			startForeground(NOTIFICATION_ID, notificationBuilder.build(jobInfo.notificationDescriptor))

			CoroutineScope(Dispatchers.IO).launch {
				withTimeoutOrNull(jobInfo.timeout) {
					try {
						jobInfo.foregroundObtainer.onForegroundObtained()
						SDKLogger.i("Task completed successfully - removing it from repo")
						pendingTasksRepository.remove(jobInfo.id)
					} catch (exception: Exception) {
						exception.message?.let { SDKLogger.e(it) }
						val newJobInfo = foregroundTaskInfo {
							id = jobInfo.id
							networkType = jobInfo.networkType
							persisted = jobInfo.persisted
							minLatencyMillis = jobInfo.minLatencyMillis
							timeout = jobInfo.timeout
							notificationDescriptor = jobInfo.notificationDescriptor
							rescheduleOnFail = jobInfo.rescheduleOnFail
							maxRetries = jobInfo.maxRetries
							retryCount = jobInfo.retryCount + 1
							foregroundObtainer = jobInfo.foregroundObtainer
						}
						if (eligibleForRescheduling.isEligible(newJobInfo)) {
							foregroundTasksScheduler.scheduleForeground(this@ForegroundTaskService, newJobInfo)
						} else {
							SDKLogger.i("max retries reached - removing it from repo")
							pendingTasksRepository.remove(jobInfo.id)
						}
					} finally {
						SDKLogger.i("Stopping foreground!")
						stopForeground(true)
					}
				}
			}
		}
		SDKLogger.d("returning START_NOT_STICKY")
		return START_NOT_STICKY
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

	private fun isConnectionAllowed(networkType: Int): Boolean {
		if (networkType == JobInfo.NETWORK_TYPE_NONE) {
			SDKLogger.i("Task doesn't require any network")
			return true
		} else {
			if (networkType == JobInfo.NETWORK_TYPE_NOT_ROAMING && mConnectivityHandler.isRoaming(this)) {
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
		const val EXTRA_TASK_ID = "com.ironsource.foreground.EXTRA_JOB_ID"
	}
}