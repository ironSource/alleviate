package com.rotemati.foregroundsdk.internal.scheduler

import android.app.PendingIntent
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec

internal interface ForegroundTasksOperationsProvider {
	fun createScheduleOperation(taskInfoSpec: TaskInfoSpec): PendingIntent
	fun createCancelOperation(taskInfoSpec: TaskInfoSpec): PendingIntent
}