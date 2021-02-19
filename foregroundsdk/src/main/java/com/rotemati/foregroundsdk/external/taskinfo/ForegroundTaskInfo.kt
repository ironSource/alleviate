package com.rotemati.foregroundsdk.external.taskinfo

import com.rotemati.foregroundsdk.external.retryepolicy.RetryData
import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import java.io.Serializable

private const val NOT_SCHEDULED_TIMESTAMP = -1L

data class ForegroundTaskInfo(
		val id: Int,
		val networkType: NetworkType,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val timeoutMillis: Long,
		val retryData: RetryData,
		val retryCount: Int = 1,
		val triggerTime: Long = NOT_SCHEDULED_TIMESTAMP
) : Serializable {

	fun isScheduled() = triggerTime != NOT_SCHEDULED_TIMESTAMP

	class Builder(private val id: Int) {
		private var networkType: NetworkType = NetworkType.None
		private var persisted: Boolean = false
		private var minLatencyMillis: Long = 0
		private var timeoutMillis: Long = Long.MAX_VALUE
		private var retryData: RetryData = RetryData(RetryPolicy.Exponential, DEFAULT_INITIAL_BACKOFF_MILLIS)

		fun networkType(networkType: NetworkType) = apply { this.networkType = networkType }
		fun persisted(persisted: Boolean) = apply { this.persisted = persisted }
		fun minLatencyMillis(minLatencyMillis: Long) = apply { this.minLatencyMillis = minLatencyMillis }
		fun timeoutMillis(timeout: Long) = apply { this.timeoutMillis = timeout }
		fun retryData(retryData: RetryData) = apply { this.retryData = retryData }

		fun build(): ForegroundTaskInfo {
			return ForegroundTaskInfo(
					id,
					networkType,
					persisted,
					minLatencyMillis,
					timeoutMillis,
					retryData
			)
		}
	}

	companion object {
		const val DEFAULT_INITIAL_BACKOFF_MILLIS = 30000L // 30 seconds.
	}
}


