package com.rotemati.foregroundsdk.foregroundtask.external.services

import android.app.Notification
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.foregroundtask.internal.exceptions.TimeoutCancellationException
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper

abstract class ForegroundTaskService : BaseForegroundTaskService() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	private lateinit var result: Result

	abstract override fun getNotification(): Notification

	abstract fun doWork(): Result

	override fun startWork(): Result {
		//todo timeout cancellation
		result = try {
			doWork()
		} catch (exception: TimeoutCancellationException) {
			exception.message?.let { logger.e(it) }
			// if timeout reached, check RetryPolicy
			exception.message?.let { logger.e(it) }
			Result.Failed(exception)
		}
		logger.d("Returning $result")
		return result
	}
}