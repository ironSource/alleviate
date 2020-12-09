package com.rotemati.foregroundsdk.foregroundtask

import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry.RetryPolicy
import com.rotemati.foregroundsdk.logger.SDKLogger
import kotlinx.coroutines.*

abstract class CoroutineForegroundTaskService : BaseForegroundTaskService() {

	private val job = Job()
	private val coroutineContext = Dispatchers.Default
	private lateinit var result: Result

	abstract suspend fun doWork(): Result

	override fun startWork(): Result {
		SDKLogger.logMethod()
		val coroutineScope = CoroutineScope(coroutineContext + job)
		coroutineScope.launch {
			withTimeoutOrNull(getForegroundTaskInfo().timeoutMillis) {
				result = try {
					doWork()
				} catch (exception: TimeoutCancellationException) {
					exception.message?.let { SDKLogger.e(it) }
					// if timeout reached, check RetryPolicy
					when (getForegroundTaskInfo().retryPolicy) {
						is RetryPolicy.NoRetry -> Result.Failed(exception)
						is RetryPolicy.Retry -> Result.Reschedule
					}
				}
			}
		}
		SDKLogger.d("Returning $result")
		return result
	}
}