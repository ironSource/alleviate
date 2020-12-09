package com.rotemati.foregroundsdk.foregroundtask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.extensions.getAlarmManager
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.latencyEpoch
import com.rotemati.foregroundsdk.logger.SDKLogger
import java.util.concurrent.TimeUnit

class ForegroundTasksScheduler(private val context: Context) {

	private val pendingTasksRepository = PendingTasksRepository(context)

	fun scheduleForeground(
			className: Class<*>,
			foregroundTaskInfo: ForegroundTaskInfo,
	) {

		val alreadyScheduled = PendingIntent.getBroadcast(context, 0,
				Intent("com.ironsource.scheduleForeground"),
				PendingIntent.FLAG_NO_CREATE) != null

		if (alreadyScheduled) {
			SDKLogger.i("Foreground task already scheduled")
			return
		}
		if (className !is BaseForegroundTaskService) {
			SDKLogger.e("Component should extend from CoroutineForegroundTaskService or ForegroundTaskService")
			return
		}

		val intent = Intent(context, className).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, foregroundTaskInfo.id)
		}

		val foregroundServicePendingIntent =
				PendingIntent.getForegroundService(context, 0, intent, 0)

		SDKLogger.i(
				"Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(
						foregroundTaskInfo.minLatencyMillis
				) + " seconds"
		)

		SDKLogger.i("Saving taskId: ${foregroundTaskInfo.id}")
		pendingTasksRepository.save(foregroundTaskInfo, className.toString())

		context.getAlarmManager().set(
				AlarmManager.RTC_WAKEUP,
				foregroundTaskInfo.latencyEpoch(),
				foregroundServicePendingIntent
		)
	}

	fun reschedule(foregroundTaskInfo: ForegroundTaskInfo) {
		if (pendingTasksRepository.contains(foregroundTaskInfo.id)) {
			SDKLogger.e("Foreground task cannot be rescheduled. There's no saved component")
			return
		}

		pendingTasksRepository.getComponent(foregroundTaskInfo.id)?.let { className ->
			scheduleForeground(className::class.java, foregroundTaskInfo)
		}
	}
}
