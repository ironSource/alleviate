package com.rotemati.foregroundsdk.internal.scheduler

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec

@RequiresApi(Build.VERSION_CODES.O)
internal class ForegroundTasksPendingIntentFactoryPost26(
		private val context: Context
) : ForegroundTasksPendingIntentFactory {

	override fun createSchedulePendingIntent(taskInfoSpec: TaskInfoSpec): PendingIntent {
		return PendingIntent.getForegroundService(context, 0, createCommonIntent(context, taskInfoSpec), 0)
	}

	override fun createCancelPendingIntent(taskInfoSpec: TaskInfoSpec): PendingIntent {
		return PendingIntent.getForegroundService(context, 0, createCommonIntent(context, taskInfoSpec), PendingIntent.FLAG_NO_CREATE)
	}
}