package com.rotemati.foregroundsdk.foregroundtask.internal

import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec

internal class UpdatedTriggerTimeCalculator(
		private val pendingTasksRepository: PendingTasksRepository
) {
	//todo check if working
	fun calculate(taskInfoSpec: TaskInfoSpec): Long {
		pendingTasksRepository.getDBItem(taskInfoSpec.foregroundTaskInfo.id)?.let { nonNullDBItem ->
			return if (taskInfoSpec.foregroundTaskInfo.shouldRunImmediately()) {
				0L
			} else {
				taskInfoSpec.foregroundTaskInfo.latencyEpoch() - nonNullDBItem.insertionTimestamp
			}
		} ?: return taskInfoSpec.foregroundTaskInfo.minLatencyMillis
	}
}