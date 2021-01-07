package com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.result

sealed class Result {
	object Success : Result()
	object Reschedule : Result()
	data class Failed(val throwable: Throwable? = null) : Result()
}