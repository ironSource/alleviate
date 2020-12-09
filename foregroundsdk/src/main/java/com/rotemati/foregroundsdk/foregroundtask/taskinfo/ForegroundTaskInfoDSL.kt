package com.rotemati.foregroundsdk.foregroundtask.taskinfo

import android.app.Notification
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry.RetryPolicy

class ForegroundTaskInfoDSL {

	//		@Deprecated(level = DeprecationLevel.ERROR, message = "only set")
//		get() = throw IllegalStateException("dsl")
	var id: Int = -1
	var networkType: NetworkType = NetworkType.None
	var persisted: Boolean = false
	var minLatencyMillis: Long = 0
	var notification: Notification? = null
	var timeoutMillis: Long = Long.MAX_VALUE
	var retryPolicy: RetryPolicy = RetryPolicy.NoRetry
	var retryCount: Int = 0

	fun build(): ForegroundTaskInfo {
		if (notification == null) {
			throw IllegalArgumentException("Notification must be set!")
		}
		return ForegroundTaskInfo(id, networkType, persisted, minLatencyMillis, notification!!, timeoutMillis, retryPolicy)
	}
}

fun foregroundTaskInfo(block: ForegroundTaskInfoDSL.() -> Unit): ForegroundTaskInfo {
	return ForegroundTaskInfoDSL().apply(block).build()
}