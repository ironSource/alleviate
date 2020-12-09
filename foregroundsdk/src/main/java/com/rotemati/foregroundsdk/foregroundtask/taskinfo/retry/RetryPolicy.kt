package com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry

sealed class RetryPolicy {
	data class Retry(
			val rescheduleOnFail: Boolean,
			val maxRetries: Int
	) : RetryPolicy()

	object NoRetry : RetryPolicy()
}