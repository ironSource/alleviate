package com.rotemati.foregroundsdk.foregroundtask.internal.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType
import java.io.Serializable

@Entity(tableName = "tasks")
internal data class ForegroundTaskInfoDBItem(
		@PrimaryKey val id: Int,
		@ColumnInfo(name = "network_type") val networkType: NetworkType,
		@ColumnInfo(name = "persisted") val persisted: Boolean,
		@ColumnInfo(name = "min_latency_millis") val minLatencyMillis: Long,
		@ColumnInfo(name = "timeout_millis") val timeoutMillis: Long,
		@ColumnInfo(name = "retry_count") val retryCount: Int,
		@ColumnInfo(name = "run_immediately") val runImmediately: Boolean,
		@ColumnInfo(name = "component_name") val componentName: String,
) : Serializable
