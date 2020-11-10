package com.rotemati.foregroundsdk.foreground

import android.app.Service
import android.app.job.JobInfo
import android.content.Intent
import com.rotemati.foregroundsdk.EligibleForRescheduling
import com.rotemati.foregroundsdk.NotificationBuilder
import com.rotemati.foregroundsdk.extensions.scheduleForeground
import com.rotemati.foregroundsdk.jobinfo.ForegroundJobInfo
import com.rotemati.foregroundsdk.jobinfo.PendingJobsRepository
import com.rotemati.foregroundsdk.logger.SDKLogger
import com.rotemati.foregroundsdk.network.ConnectivityEventsHandler
import com.rotemati.foregroundsdk.network.ConnectivityEventsHandlerImpl
import com.rotemati.foregroundsdk.notification.DefaultNotificationDescriptorCreator
import com.rotemati.foregroundsdk.notification.NotificationChannelsCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor
import kotlinx.coroutines.*

private const val NOTIFICATION_ID: Int = 654321
private const val JOB_ID_NOT_VALID: Int = -1

class ForegroundService : Service() {

	private lateinit var connectivityEventsHandler: ConnectivityEventsHandler
	private lateinit var pendingJobsRepository: PendingJobsRepository
	private lateinit var eligibleForRescheduling: EligibleForRescheduling
	private lateinit var defaultNotificationDescriptorCreator: DefaultNotificationDescriptorCreator
	private lateinit var notificationBuilder: NotificationBuilder

	override fun onCreate() {
		super.onCreate()
		connectivityEventsHandler = ConnectivityEventsHandlerImpl(this)
		connectivityEventsHandler.register()
		pendingJobsRepository = PendingJobsRepository(this)
		eligibleForRescheduling = EligibleForRescheduling()
		defaultNotificationDescriptorCreator = DefaultNotificationDescriptorCreator()
		notificationBuilder = NotificationBuilder(this, NotificationChannelsCreator(this))
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		SDKLogger.logMethod()
		intent?.let { nonNullIntent ->
			val jobId = nonNullIntent.getIntExtra(EXTRA_JOB_ID, JOB_ID_NOT_VALID)
			if (jobId == JOB_ID_NOT_VALID) {
				onError("job id not valid")
				return START_NOT_STICKY
			}
			if (!pendingJobsRepository.contains(jobId)) {
				onError("job isn't in the repo")
				return START_NOT_STICKY
			}
			val jobInfo = pendingJobsRepository.pendingForegroundJobs.find { it.id == jobId }

			if (jobInfo == null) {
				//todo decide what to do in this case
				onError("jobInfo == null")
				return START_NOT_STICKY
			}

			if (!isConnectionAllowed(jobInfo.networkType)) {
				//todo decide what to do - maybe start connectivity job service to wake up when there's internet connection
				onError("connection type isn't allowed")
				return START_NOT_STICKY
			}
			startForeground(NOTIFICATION_ID, notificationBuilder.build(jobInfo.notificationDescriptor))

			SDKLogger.d("jobInfo.retryCount: ${jobInfo.retryCount}")
			SDKLogger.d("jobInfo.maxRetries: ${jobInfo.maxRetries}")
			SDKLogger.d("jobInfo.timeout: ${jobInfo.timeout}")

			CoroutineScope(Dispatchers.IO).launch {
				withTimeoutOrNull(jobInfo.timeout) {
					try {
//                        jobInfo.foregroundObtainer.onForegroundObtained()
						delay(120000)
					} catch (exception: Exception) {
						exception.message?.let { SDKLogger.e(it) }
						val newJobInfo = ForegroundJobInfo(
								id = jobInfo.id,
								networkType = jobInfo.networkType,
								persisted = jobInfo.persisted,
								minLatencyMillis = jobInfo.minLatencyMillis,
								timeout = jobInfo.timeout,
								notificationDescriptor = jobInfo.notificationDescriptor,
//                                      foregroundObtainer = ReposForegroundObtainer(),
								rescheduleOnFail = jobInfo.rescheduleOnFail,
								maxRetries = jobInfo.maxRetries,
								retryCount = jobInfo.retryCount + 1
						)
						if (eligibleForRescheduling.isEligible(newJobInfo)) {
							scheduleForeground(this@ForegroundService, newJobInfo)
						} else {
							SDKLogger.i("max retries reached - removing job from repo")
							pendingJobsRepository.remove(jobInfo.id)
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

	private fun onError(error: String, notificationDescriptor: NotificationDescriptor = defaultNotificationDescriptorCreator.create()) {
		SDKLogger.e(error)
		startForeground(NOTIFICATION_ID, notificationBuilder.build(notificationDescriptor))
		stopForeground(true)
		stopSelf()
	}

	private fun isConnectionAllowed(networkType: Int): Boolean {
		when (networkType) {
			JobInfo.NETWORK_TYPE_ANY -> {
				if (connectivityEventsHandler.isBlocked) {
					SDKLogger.e("Required any network but network is blocked!")
					return false
				}
				if (!connectivityEventsHandler.isConnected(this)) {
					SDKLogger.i("Required any network type but no internet connection!")
					return false
				}
			}
		}
		return true
	}

	override fun onBind(intent: Intent?): Nothing? = null

	companion object {
		//		const val EXTRA_JOB_INFO = "com.ironsource.foreground.EXTRA_JOB_INFO"
		const val EXTRA_JOB_ID = "com.ironsource.foreground.EXTRA_JOB_ID"
	}
}