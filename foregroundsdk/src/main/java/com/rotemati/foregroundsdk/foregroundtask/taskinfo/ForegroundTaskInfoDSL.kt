package com.rotemati.foregroundsdk.foregroundtask.taskinfo

import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType

class ForegroundTaskInfoDSL {

	//		@Deprecated(level = DeprecationLevel.ERROR, message = "only set")
//		get() = throw IllegalStateException("dsl")
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