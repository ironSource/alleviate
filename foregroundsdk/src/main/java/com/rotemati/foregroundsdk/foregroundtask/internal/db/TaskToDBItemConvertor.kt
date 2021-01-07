package com.rotemati.foregroundsdk.foregroundtask.internal.db

import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo

class TaskToDBItemConvertor {
	fun toDBItem(foregroundTaskInfo: ForegroundTaskInfo, componentName: String): ForegroundTaskInfoDBItem {
		return ForegroundTaskInfoDBItem(
				foregroundTaskInfo.id,
				foregroundTaskInfo.networkType,
				foregroundTaskInfo.persisted,
				foregroundTaskInfo.minLatencyMillis,
				foregroundTaskInfo.timeoutMillis,
				foregroundTaskInfo.retryCount,
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
				foregroundTaskInfoDBItem.retryCount
		)
	}
}