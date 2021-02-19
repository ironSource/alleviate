package com.rotemati.foregroundsdk.external.services

import android.app.Notification
import com.rotemati.foregroundsdk.external.ForegroundSdk
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.stopinfo.StoppedCause
import com.rotemati.foregroundsdk.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class ForegroundTaskService : BaseForegroundTaskService() {

	private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSdk.logger)

	val retryCount: Int
		get() = taskInfoSpec.foregroundTaskInfo.retryCount

	abstract override fun getNotification(): Notification

	abstract fun doWork(): Result

	abstract fun onStop(stoppedCause: StoppedCause): Result

	override fun doStop(stoppedCause: StoppedCause): Result {
		return onStop(stoppedCause)
	}

	override fun startWork(): Result {
		return try {
			executorService.submit(Callable {
				doWork()
			}).get(taskInfoSpec.foregroundTaskInfo.timeoutMillis, TimeUnit.MILLISECONDS)
		} catch (e: Exception) {
			doStop(StoppedCause.Timeout)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		executorService.shutdown()
	}
}