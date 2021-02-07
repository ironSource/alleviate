package com.rotemati.foregroundsdk.external.taskinfo.result

import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy

sealed class Result {
	object Success : Result()
	data class Reschedule(val retryPolicy: RetryPolicy) : Result()
	object Failed : Result()
	internal object AlreadyFinished : Result()
}