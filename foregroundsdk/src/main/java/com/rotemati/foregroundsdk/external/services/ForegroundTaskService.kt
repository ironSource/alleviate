package com.rotemati.foregroundsdk.external.services

import android.app.Notification
import com.rotemati.foregroundsdk.external.stopinfo.StoppedCause
import com.rotemati.foregroundsdk.external.taskinfo.result.Result
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class ForegroundTaskService : BaseForegroundTaskService() {

	private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

	abstract override fun getNotification(): Notification

	abstract fun doWork(): Result

	abstract fun onStop(stoppedCause: StoppedCause): Result

	override fun doStop(stoppedCause: StoppedCause): Result {
		return if (!executorService.isShutdown) {
			onStop(stoppedCause).also { executorService.shutdownNow() }
		} else {
			Result.AlreadyFinished
		}
	}

	override fun startWork(): Result {
		val callable = Callable {
			doWork()
		}
		val future = executorService.submit(callable)
		return try {
			future.get(foregroundTaskInfo.timeoutMillis, TimeUnit.MILLISECONDS)
		} catch (e: Exception) {
			future.cancel(true)
			onStop(StoppedCause.Timeout)
		} finally {
			executorService.shutdownNow()
		}
	}
}