package com.rotemati.foregroundsdk.foregroundtask.taskinfo

import android.app.Notification
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry.RetryPolicy

class ForegroundTaskInfo(
		val id: Int,
		val networkType: NetworkType,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val notification: Notification,
		val timeoutMillis: Long,
		val retryPolicy: RetryPolicy,
		val retryCount: Int = 0
) {

	class Builder {
		private var id: Int = -1
		private var networkType: NetworkType = NetworkType.None
		private var persisted: Boolean = false
		private var minLatencyMillis: Long = 0
		private var notification: Notification? = null
		private var timeoutMillis: Long = Long.MAX_VALUE
		private var retryPolicy: RetryPolicy = RetryPolicy.NoRetry

		fun id(id: Int) = apply { this.id = id }
		fun networkType(networkType: NetworkType) = apply { this.networkType = networkType }
		fun persisted(persisted: Boolean) = apply { this.persisted = persisted }
		fun minLatencyMillis(minLatencyMillis: Long) = apply { this.minLatencyMillis = minLatencyMillis }
		fun timeoutMillis(timeout: Long) = apply { this.timeoutMillis = timeout }
		fun notification(notification: Notification) = apply { this.notification = notification }
		fun retryPolicy(retryPolicy: RetryPolicy) = apply { this.retryPolicy = retryPolicy }

		fun build(): ForegroundTaskInfo {
			if (notification == null) {
				throw IllegalArgumentException("Notification must be set!")
			}
			return ForegroundTaskInfo(id, networkType, persisted, minLatencyMillis, notification!!, timeoutMillis, retryPolicy)
		}
	}
}

fun ForegroundTaskInfo.latencyEpoch() = System.currentTimeMillis() + minLatencyMillis

