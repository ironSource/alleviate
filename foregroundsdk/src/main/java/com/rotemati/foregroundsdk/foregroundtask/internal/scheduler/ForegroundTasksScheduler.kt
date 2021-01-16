package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec

internal interface ForegroundTasksScheduler {
	fun schedule(taskInfoSpec: TaskInfoSpec)
	fun cancel(taskInfoSpec: TaskInfoSpec)
}