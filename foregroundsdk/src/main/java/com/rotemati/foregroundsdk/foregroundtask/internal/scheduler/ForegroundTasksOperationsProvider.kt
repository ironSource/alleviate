package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import android.app.PendingIntent
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec

internal interface ForegroundTasksOperationsProvider {
	fun createScheduleOperation(taskInfoSpec: TaskInfoSpec): PendingIntent
	fun createCancelOperation(taskInfoSpec: TaskInfoSpec): PendingIntent
}