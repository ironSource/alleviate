package com.rotemati.foregroundsdk.foregroundtask.taskinfo.result

sealed class Result {
	object Success : Result()
	object Reschedule : Result()
	data class Failed(val exception: Exception) : Result()
}