package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.services.BaseForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getAlarmManager
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getStandbyBucket
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import java.util.concurrent.TimeUnit

private const val STANDBY_BUCKET_NEVER = 50

internal class ForegroundTasksSchedulerPost26(
		private val context: Context
) : ForegroundTasksScheduler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	@RequiresApi(Build.VERSION_CODES.O)
	override fun schedule(taskInfoSpec: TaskInfoSpec) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			if (context.getStandbyBucket() == STANDBY_BUCKET_NEVER) {
				logger.d("App is in 'Never' bucket")
				if (taskInfoSpec.foregroundTaskInfo.shouldRunImmediately()) {

				}
			}
		}

		val intent = Intent(context, Class.forName(taskInfoSpec.componentName)).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
		}
		val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)
		logger.i("Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(taskInfoSpec.foregroundTaskInfo.minLatencyMillis) + " seconds")

		val triggerAtMillis = if (taskInfoSpec.foregroundTaskInfo.shouldRunImmediately()) {
			0
		} else {
			taskInfoSpec.foregroundTaskInfo.latencyEpoch()
		}
		context.getAlarmManager().set(
				AlarmManager.RTC_WAKEUP,
				triggerAtMillis,
				foregroundServicePendingIntent)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	override fun cancel(foregroundTaskInfo: ForegroundTaskInfo) {
//		pendingTasksRepository.getTaskInfoSpec(foregroundTaskInfo.id)?.let {
//			val intent = Intent(context, Class.forName(it.componentName)).apply {
//				putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, foregroundTaskInfo.id)
//			}
//			val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)
//			context.getAlarmManager().cancel(foregroundServicePendingIntent)
//		}
	}
}