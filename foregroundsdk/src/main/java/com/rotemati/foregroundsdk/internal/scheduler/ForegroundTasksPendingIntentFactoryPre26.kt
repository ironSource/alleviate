package com.rotemati.foregroundsdk.internal.scheduler

import android.app.PendingIntent
import android.content.Context
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec

internal class ForegroundTasksPendingIntentFactoryPre26(
		private val context: Context
) : ForegroundTasksPendingIntentFactory {

	override fun createSchedulePendingIntent(taskInfoSpec: TaskInfoSpec): PendingIntent {
		return PendingIntent.getService(context, 0, createCommonIntent(context, taskInfoSpec), 0)
	}

	override fun createCancelPendingIntent(taskInfoSpec: TaskInfoSpec): PendingIntent {
		return PendingIntent.getService(context, 0, createCommonIntent(context, taskInfoSpec), PendingIntent.FLAG_NO_CREATE)
	}
}