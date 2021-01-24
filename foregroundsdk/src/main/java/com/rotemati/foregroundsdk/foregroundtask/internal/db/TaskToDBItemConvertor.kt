package com.rotemati.foregroundsdk.foregroundtask.internal.db

import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo

internal class TaskToDBItemConvertor {
	fun toDBItem(timestamp: Long, foregroundTaskInfo: ForegroundTaskInfo, componentName: String): ForegroundTaskInfoDBItem {
		return ForegroundTaskInfoDBItem(
				foregroundTaskInfo.id,
				foregroundTaskInfo.networkType,
				foregroundTaskInfo.persisted,
				foregroundTaskInfo.minLatencyMillis,
				foregroundTaskInfo.timeoutMillis,
				foregroundTaskInfo.retryCount,
				foregroundTaskInfo.runImmediately,
				timestamp,
				componentName
		)
	}

	fun fromDBItem(foregroundTaskInfoDBItem: ForegroundTaskInfoDBItem): ForegroundTaskInfo {
		return ForegroundTaskInfo(
				foregroundTaskInfoDBItem.id,
				foregroundTaskInfoDBItem.networkType,
				foregroundTaskInfoDBItem.persisted,
				foregroundTaskInfoDBItem.minLatencyMillis,
				foregroundTaskInfoDBItem.timeoutMillis,
				foregroundTaskInfoDBItem.retryCount,
				foregroundTaskInfoDBItem.runImmediately
		)
	}
}