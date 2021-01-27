package com.rotemati.foregroundsdk.internal.bucketpolling

import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.external.ForegroundSDK
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import kotlin.properties.Delegates.notNull

private const val STANDBY_BUCKET_NEVER = 50

@RequiresApi(Build.VERSION_CODES.P)
internal object BucketPoller {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val handler = Handler()
	private var foregroundServiceStartTime: Long by notNull()
	var pollingData = ForegroundSDK.bucketPollingData
	var strategy: BucketPollingStrategy by notNull()
	var onNoLongerNeeded: (String) -> Unit = { reason ->
		logger.d(reason)
	}

	val inNeverBucket: Boolean
		get() = bucket == STANDBY_BUCKET_NEVER

	val bucket: Int
		get() = strategy.getCurrentBucket()

	fun start() {
		foregroundServiceStartTime = System.currentTimeMillis()
		verifyBucket()
	}

	private fun schedule() {
		logger.d("scheduling to run in ${pollingData.interval} millis")
		handler.postDelayed({
			verifyBucket()
		}, pollingData.interval)
	}

	private fun verifyBucket() {
		if (isPollingTimeoutPassed()) {
			onNoLongerNeeded("Polling timeout passed")
			return
		}
		if (!inNeverBucket) {
			onNoLongerNeeded("Bucket is not 'never'")
			return
		}
		logger.d("Should keep polling")
		schedule()
	}

	private fun isPollingTimeoutPassed(): Boolean {
		return System.currentTimeMillis() - foregroundServiceStartTime >= pollingData.timeout
	}
}