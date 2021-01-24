package com.rotemati.foregroundsdk.foregroundtask.external.taskinfo

import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType
import java.io.Serializable

private const val NOT_SCHEDULED_TIMESTAMP = -1L

data class ForegroundTaskInfo(
		val id: Int,
		val networkType: NetworkType,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val timeoutMillis: Long,
		val retryCount: Int = 0,
		val triggerTime: Long = NOT_SCHEDULED_TIMESTAMP
) : Serializable {

	fun isScheduled() = triggerTime != NOT_SCHEDULED_TIMESTAMP

	class Builder {
		private var id: Int = -1
		private var networkType: NetworkType = NetworkType.None
		private var persisted: Boolean = false
		private var minLatencyMillis: Long = 0
		private var timeoutMillis: Long = Long.MAX_VALUE

		fun id(id: Int) = apply { this.id = id }
		fun networkType(networkType: NetworkType) = apply { this.networkType = networkType }
		fun persisted(persisted: Boolean) = apply { this.persisted = persisted }
		fun minLatencyMillis(minLatencyMillis: Long) = apply { this.minLatencyMillis = minLatencyMillis }
		fun timeoutMillis(timeout: Long) = apply { this.timeoutMillis = timeout }

		fun build(): ForegroundTaskInfo {
			return ForegroundTaskInfo(
					id,
					networkType,
					persisted,
					minLatencyMillis,
					timeoutMillis
			)
		}
	}
}


