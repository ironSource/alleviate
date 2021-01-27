package com.rotemati.foregroundsdk.external.services

import android.app.Notification
import com.rotemati.foregroundsdk.external.ForegroundSDK
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import java.util.concurrent.*

abstract class ForegroundTaskService : BaseForegroundTaskService() {

	private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

	abstract override fun getNotification(): Notification

	abstract fun doWork(): Result

	abstract fun onTimeout(): Result

	abstract fun onError(e: Exception): Result

	override fun startWork(): Result {
		val callable = Callable {
			doWork()
		}
		val future = executorService.submit(callable)
		return try {
			future.get(foregroundTaskInfo.timeoutMillis, TimeUnit.MILLISECONDS)
		} catch (e: Exception) {
			future.cancel(true)
			when (e) {
				is TimeoutException -> onTimeout()
				else -> onError(e)
			}
		}
	}
}