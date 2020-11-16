package com.rotemati.foregroundsdk.foregroundtask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.extensions.getAlarmManager
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.latencyEpoch
import com.rotemati.foregroundsdk.logger.SDKLogger
import java.util.concurrent.TimeUnit

fun scheduleForeground(
		context: Context,
		foregroundTaskInfo: ForegroundTaskInfo,
) {

	SDKLogger.d("new retry count: ${foregroundTaskInfo.retryCount}")
	val intent = Intent(context, ForegroundService::class.java).apply {
		putExtra(ForegroundService.EXTRA_TASK_ID, foregroundTaskInfo.id)
	}

	val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)

	SDKLogger.i(
			"Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(
					foregroundTaskInfo.minLatencyMillis
			) + " seconds"
	)

	val pendingTasksRepository = PendingTasksRepository(context)
	SDKLogger.i("Saving taskId: ${foregroundTaskInfo.id}")
	pendingTasksRepository.save(foregroundTaskInfo)

	context.getAlarmManager().set(
			AlarmManager.RTC_WAKEUP,
			foregroundTaskInfo.latencyEpoch(),
			foregroundServicePendingIntent
	)
}