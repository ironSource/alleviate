package com.rotemati.foregroundsdk.bucketpolling

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.extensions.getStandbyBucket
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext

class BucketPollerImpl(
		private val context: Context,
		private val bucketPollingDelay: Long,
		private val timeOut: Long,
		private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
) : BucketPoller {

	private var coroutineContext: CoroutineContext? = null

	@RequiresApi(Build.VERSION_CODES.P)
	override fun poll(): Flow<Int> {
		return flow {
			coroutineContext = currentCoroutineContext()
			withTimeoutOrNull(timeOut) {
				try {
					while (true) {
						val standbyBucket = context.getStandbyBucket()
						logger.i("StandbyBucket: $standbyBucket")
						emit(standbyBucket)
						delay(bucketPollingDelay)
					}
				} catch (exception: Throwable) {
					exception.message?.let { logger.e(it) }
				}
			}
		}
	}

	override fun stop() {
		coroutineContext?.cancel()
	}
}