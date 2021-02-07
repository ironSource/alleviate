package com.rotemati.foregroundsdk.internal.datastore

import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import java.io.Serializable

internal data class ForegroundTaskInfoDBItem(
		val id: Int,
		val networkType: NetworkType,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val timeoutMillis: Long,
		val retryCount: Int,
		val triggerTime: Long,
		val componentName: String
) : Serializable
