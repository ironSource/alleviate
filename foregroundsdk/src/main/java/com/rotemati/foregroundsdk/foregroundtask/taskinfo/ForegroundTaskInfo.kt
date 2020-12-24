package com.rotemati.foregroundsdk.foregroundtask.taskinfo

import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import java.io.Serializable

data class ForegroundTaskInfo(
		val id: Int,
		val networkType: NetworkType,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val timeoutMillis: Long,
		val retryCount: Int = 0
) : Serializable {

	fun latencyEpoch() = System.currentTimeMillis() + minLatencyMillis

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


