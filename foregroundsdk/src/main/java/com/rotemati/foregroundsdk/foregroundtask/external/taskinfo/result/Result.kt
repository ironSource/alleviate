package com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.result

import com.rotemati.foregroundsdk.foregroundtask.external.retryepolicy.RetryPolicy

sealed class Result {
	object Success : Result()
	data class Reschedule(val retryPolicy: RetryPolicy) : Result()
	object Failed : Result()
}