package com.rotemati.foregroundsdk.external

import android.content.Context
import com.rotemati.foregroundsdk.external.bucketpolling.BucketPollingData
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.internal.logger.DefaultSDKLogger
import java.util.concurrent.TimeUnit

object ForegroundSDK {
	var foregroundLogger: ForegroundLogger = DefaultSDKLogger
	lateinit var context: Context
	var bucketPollingData = BucketPollingData(
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.MINUTES.toMillis(5)
    )
}