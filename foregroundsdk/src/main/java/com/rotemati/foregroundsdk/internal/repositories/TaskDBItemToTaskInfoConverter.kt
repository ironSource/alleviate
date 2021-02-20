package com.rotemati.foregroundsdk.internal.repositories

import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.internal.datastore.ForegroundTaskInfoDBItem

internal class TaskDBItemToTaskInfoConverter {

	fun toTaskInfo(dbItem: ForegroundTaskInfoDBItem): TaskInfoSpec {
		return TaskInfoSpec(
				ForegroundTaskInfo(id = dbItem.id,
						networkType = dbItem.networkType,
						persisted = dbItem.persisted,
						minLatencyMillis = dbItem.minLatencyMillis,
						retryCount = dbItem.retryCount,
						triggerTime = dbItem.triggerTime,
						timeoutMillis = dbItem.timeoutMillis,
						retryData = dbItem.retryData
				),
				componentName = dbItem.componentName
		)
	}

	fun toDBItem(taskInfoSpec: TaskInfoSpec): ForegroundTaskInfoDBItem {
		return ForegroundTaskInfoDBItem(
				id = taskInfoSpec.foregroundTaskInfo.id,
				networkType = taskInfoSpec.foregroundTaskInfo.networkType,
				persisted = taskInfoSpec.foregroundTaskInfo.persisted,
				minLatencyMillis = taskInfoSpec.foregroundTaskInfo.minLatencyMillis,
				retryCount = taskInfoSpec.foregroundTaskInfo.retryCount,
				triggerTime = taskInfoSpec.foregroundTaskInfo.triggerTime,
				componentName = taskInfoSpec.componentName,
				timeoutMillis = taskInfoSpec.foregroundTaskInfo.timeoutMillis,
				retryData = taskInfoSpec.foregroundTaskInfo.retryData
		)
	}
}