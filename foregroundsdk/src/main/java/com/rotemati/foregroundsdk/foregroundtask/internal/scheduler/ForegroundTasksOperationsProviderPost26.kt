package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.external.services.BaseForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec

@RequiresApi(Build.VERSION_CODES.O)
internal class ForegroundTasksOperationsProviderPost26(
		private val context: Context
) : ForegroundTasksOperationsProvider {

	override fun createScheduleOperation(taskInfoSpec: TaskInfoSpec): PendingIntent {
		val intent = Intent(context, Class.forName(taskInfoSpec.componentName)).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
		}
		return PendingIntent.getForegroundService(context, 0, intent, 0)
	}

	override fun createCancelOperation(taskInfoSpec: TaskInfoSpec): PendingIntent {
		val intent = Intent(context, Class.forName(taskInfoSpec.componentName)).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
		}
		return PendingIntent.getForegroundService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
	}
}