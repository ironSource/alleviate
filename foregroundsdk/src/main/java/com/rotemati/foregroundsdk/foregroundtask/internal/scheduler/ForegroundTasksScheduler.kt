package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec

internal interface ForegroundTasksScheduler {
	fun schedule(taskInfoSpec: TaskInfoSpec)
	fun cancel(foregroundTaskInfo: ForegroundTaskInfo)
}