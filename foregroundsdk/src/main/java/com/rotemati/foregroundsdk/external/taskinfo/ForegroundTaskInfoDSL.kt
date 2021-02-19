package com.rotemati.foregroundsdk.external.taskinfo

import com.ironsource.aura.dslint.annotations.DSLMandatory
import com.ironsource.aura.dslint.annotations.DSLint
import com.rotemati.foregroundsdk.external.retryepolicy.RetryData
import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy
import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo.Companion.DEFAULT_INITIAL_BACKOFF_MILLIS
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType

@DSLint
class ForegroundTaskInfoDSL {

	@set:DSLMandatory(message = "Task ID must be set")
	var id: Int = -1
	var networkType: NetworkType = NetworkType.None
	var persisted: Boolean = false
	var minLatencyMillis: Long = 0
	var timeoutMillis: Long = Long.MAX_VALUE
	var retryData: RetryData = RetryData(RetryPolicy.Exponential, DEFAULT_INITIAL_BACKOFF_MILLIS)

	fun build(): ForegroundTaskInfo {
		return ForegroundTaskInfo(id, networkType, persisted, minLatencyMillis, timeoutMillis, retryData)
	}
}

fun foregroundTaskInfo(block: ForegroundTaskInfoDSL.() -> Unit): ForegroundTaskInfo {
	return ForegroundTaskInfoDSL().apply(block).build()
}