package com.rotemati.foregroundsdk.internal.db

import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo

internal class TaskToDBItemConvertor {
	fun toDBItem(foregroundTaskInfo: ForegroundTaskInfo, componentName: String): ForegroundTaskInfoDBItem {
		return ForegroundTaskInfoDBItem(
				foregroundTaskInfo.id,
				foregroundTaskInfo.networkType,
				foregroundTaskInfo.persisted,
				foregroundTaskInfo.minLatencyMillis,
				foregroundTaskInfo.timeoutMillis,
				foregroundTaskInfo.retryCount,
				foregroundTaskInfo.triggerTime,
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
				foregroundTaskInfoDBItem.triggerTime
		)
	}
}