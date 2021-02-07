package com.rotemati.foregroundsdk.external.taskinfo

import com.ironsource.aura.dslint.annotations.DSLMandatory
import com.ironsource.aura.dslint.annotations.DSLint
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType

@DSLint
class ForegroundTaskInfoDSL {

	@set:DSLMandatory(message = "Task ID must be set")
	var id: Int = -1
	var networkType: NetworkType = NetworkType.None
	var persisted: Boolean = false
	var minLatencyMillis: Long = 0
	var timeoutMillis: Long = Long.MAX_VALUE

	fun build(): ForegroundTaskInfo {
		return ForegroundTaskInfo(id, networkType, persisted, minLatencyMillis, timeoutMillis)
	}
}

fun foregroundTaskInfo(block: ForegroundTaskInfoDSL.() -> Unit): ForegroundTaskInfo {
	return ForegroundTaskInfoDSL().apply(block).build()
}