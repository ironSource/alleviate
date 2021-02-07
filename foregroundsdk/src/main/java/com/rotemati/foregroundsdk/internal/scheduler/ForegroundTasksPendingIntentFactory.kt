package com.rotemati.foregroundsdk.internal.scheduler

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.external.services.BaseForegroundTaskService
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec

internal interface ForegroundTasksPendingIntentFactory {
	fun createCommonIntent(context: Context, taskInfoSpec: TaskInfoSpec) = Intent(context, Class.forName(taskInfoSpec.componentName)).apply {
		putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
	}

	fun createSchedulePendingIntent(taskInfoSpec: TaskInfoSpec): PendingIntent
	fun createCancelPendingIntent(taskInfoSpec: TaskInfoSpec): PendingIntent
}