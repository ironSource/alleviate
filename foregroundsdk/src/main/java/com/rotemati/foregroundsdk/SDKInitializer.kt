package com.rotemati.foregroundsdk

import android.app.Notification
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.ironsource.aura.dslint.annotations.DSLint
import com.rotemati.foregroundsdk.bucketpolling.BucketPollerImpl
import com.rotemati.foregroundsdk.logger.SDKLogger
import com.rotemati.foregroundsdk.network.ConnectivityEventsHandler
import com.rotemati.foregroundsdk.network.ConnectivityEventsHandlerImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@DSLint
class SDKInitializer(
    private var context: Context,
    private val bucketPollingTimeout: Long,
    private val notification: Notification,
    bucketPollingDelay: Long,
) {

    private val bucketPoller = BucketPollerImpl(context, bucketPollingDelay)
    private val connectivityEventsHandler: ConnectivityEventsHandler

    init {
        SDKLogger.logMethod()
        connectivityEventsHandler = ConnectivityEventsHandlerImpl(context)
        connectivityEventsHandler.register()
    }

    fun start() {
        SDKLogger.d("hasInternetAccess: ${connectivityEventsHandler.hasInternetAccess}")
        SDKLogger.d("isBlocked: ${connectivityEventsHandler.isBlocked}")
        if (connectivityEventsHandler.hasInternetAccess && connectivityEventsHandler.isBlocked) {
//			notificationDisplayer.display(notification)
        } else {
            SDKLogger.i("No need to initialize SDK.")
            finish()
        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            SDKLogger.i("No need to initialize SDK. Android OS version is ${Build.VERSION.SDK_INT}")
//            return
//        }
//        val standbyBucket = context.getStandbyBucket()
//        if (standbyBucket < UsageStatsManager.STANDBY_BUCKET_RARE) {
//            SDKLogger.i("No need to initialize SDK. Android Standby buckets $standbyBucket is valid")
//            return
//        }
//        startBucketPolling()
    }

    fun finish() {
//		notificationDisplayer.dismiss()
//        bucketPoller.stop()
        connectivityEventsHandler.unregister()
    }

    /**
     * Will poll until one of the following occurs:
     * 1. Polling timeout reached.
     * 2. Stand by bucket has changed to less than RARE (40)
     * 3. Caller job finished - meaning the connectivity is no longer needed
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun startBucketPolling() {
        CoroutineScope(Dispatchers.IO).launch {
            withTimeoutOrNull(bucketPollingTimeout) {
                bucketPoller.poll().collect { value ->
                    if (isActive && value < UsageStatsManager.STANDBY_BUCKET_RARE) {
                        finish()
                    }
                }
            }
        }
    }
}