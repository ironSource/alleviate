package com.rotemati.foregroundsdk.foreground

import android.app.Service
import android.app.job.JobInfo
import android.content.Intent
import com.rotemati.foregroundsdk.NotificationBuilder
import com.rotemati.foregroundsdk.Parcelable.Parcelables
import com.rotemati.foregroundsdk.extensions.scheduleForeground
import com.rotemati.foregroundsdk.jobinfo.ForegroundJobInfo
import com.rotemati.foregroundsdk.jobinfo.PendingJobsRepository
import com.rotemati.foregroundsdk.logger.SDKLogger
import com.rotemati.foregroundsdk.network.ConnectivityEventsHandler
import com.rotemati.foregroundsdk.network.ConnectivityEventsHandlerImpl
import kotlinx.coroutines.*

private const val NOTIFICATION_ID: Int = 654321

class ForegroundService : Service() {

    private lateinit var connectivityEventsHandler: ConnectivityEventsHandler
    private lateinit var pendingJobsRepository: PendingJobsRepository

    override fun onCreate() {
        super.onCreate()
        connectivityEventsHandler = ConnectivityEventsHandlerImpl(this)
        connectivityEventsHandler.register()
        pendingJobsRepository = PendingJobsRepository(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        SDKLogger.logMethod()
        intent?.let { nonNullIntent ->
            val jobInfoByteArray = nonNullIntent.getByteArrayExtra(EXTRA_JOB_INFO)
            val jobInfo = Parcelables.toParcelable(jobInfoByteArray, ForegroundJobInfo.CREATOR)

            if (jobInfo == null) {
                //todo decide what to do in this case
                onError("jobInfo == null")
                return START_NOT_STICKY
            }

            val notification = jobInfo.notification
            if (!isConnectionAllowed(jobInfo.networkType)) {
                //todo decide what to do - maybe start connectivity job service to wake up when there's internet connection
                onError("connection type isn't allowed")
                return START_NOT_STICKY
            }

            startForeground(NOTIFICATION_ID, notification)

            SDKLogger.d("jobInfo.retryCount: ${jobInfo.retryCount}")
            SDKLogger.d("jobInfo.maxRetries: ${jobInfo.maxRetries}")

            CoroutineScope(Dispatchers.IO).launch {
                withTimeoutOrNull(jobInfo.timeout) {
                    try {
//                        jobInfo.foregroundObtainer.onForegroundObtained()
                        delay(120000)
                    } catch (exception: Exception) {
                        exception.message?.let { SDKLogger.e(it) }
                        val newRetryCount = jobInfo.retryCount + 1
                        SDKLogger.d("newRetryCount: $newRetryCount")
                        if (newRetryCount > jobInfo.maxRetries) {
                            SDKLogger.i("max retries reached - removing job from repo")
                            pendingJobsRepository.remove(jobInfo.id)
                        } else {
                            if (jobInfo.rescheduleOnFail) {
                                scheduleForeground(
                                    this@ForegroundService, ForegroundJobInfo(
                                        id = jobInfo.id,
                                        networkType = jobInfo.networkType,
                                        isPersisted = jobInfo.isPersisted,
                                        minLatencyMillis = jobInfo.minLatencyMillis,
                                        timeout = jobInfo.timeout,
                                        notification = jobInfo.notification,
//                                      foregroundObtainer = ReposForegroundObtainer(),
                                        rescheduleOnFail = jobInfo.rescheduleOnFail,
                                        maxRetries = jobInfo.maxRetries,
                                        retryCount = newRetryCount
                                    )
                                )
                            }
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

    private fun onError(error: String) {
        SDKLogger.e(error)
        val defaultNotification = NotificationBuilder(this).build()
        startForeground(NOTIFICATION_ID, defaultNotification)
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
        const val EXTRA_JOB_INFO = "com.ironsource.foreground.EXTRA_JOB_INFO"
    }
}