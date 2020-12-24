package com.rotemati.foregroundsdk.foregroundtask.services

import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

abstract class CoroutineForegroundTaskService : BaseForegroundTaskService() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	private lateinit var result: Result

	abstract suspend fun doWork(): Result

	override fun startWork(): Result {
		runBlocking {
			launch {
				withTimeoutOrNull(getForegroundTaskInfo().timeoutMillis) {
					result = try {
						doWork()
					} catch (exception: TimeoutCancellationException) {
						exception.message?.let { logger.e(it) }
						Result.Failed(exception)
					}
				}
			}
		}
		return result
	}
}