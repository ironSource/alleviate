package com.rotemati.foregroundsdk.foregroundtask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.extensions.getAlarmManager
import com.rotemati.foregroundsdk.extensions.getStandbyBucket
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.services.BaseForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import java.util.concurrent.TimeUnit

private const val STANDBY_BUCKET_NEVER = 50

class ForegroundTasksScheduler(private val context: Context) {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	private val pendingTasksRepository = PendingTasksRepository(context)

	fun scheduleForeground(
			className: Class<*>,
			foregroundTaskInfo: ForegroundTaskInfo,
	) {

		val alreadyScheduled = PendingIntent.getBroadcast(context, 0,
				Intent("com.ironsource.scheduleForeground"),
				PendingIntent.FLAG_NO_CREATE) != null

		if (alreadyScheduled) {
			logger.i("Foreground task already scheduled")
			return
		}
//		if (className.superclass !is BaseForegroundTaskService) {
//			SDKLogger.e("Component should extend from CoroutineForegroundTaskService or ForegroundTaskService")
//			return
//		}
		if (context.getStandbyBucket() == STANDBY_BUCKET_NEVER) {
			logger.d("App is in 'Never' bucket")
			if (foregroundTaskInfo.minLatencyMillis == 0L) {

			}
		}

		val intent = Intent(context, className).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, foregroundTaskInfo.id)
		}

		val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)

		logger.i(
				"Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(
						foregroundTaskInfo.minLatencyMillis
				) + " seconds"
		)

		logger.i("Saving taskId: ${foregroundTaskInfo.id}")
		pendingTasksRepository.save(foregroundTaskInfo, className.toString())

		context.getAlarmManager().set(
				AlarmManager.RTC_WAKEUP,
				foregroundTaskInfo.latencyEpoch(),
				foregroundServicePendingIntent
		)
	}

	fun reschedule(foregroundTaskInfo: ForegroundTaskInfo) {
		if (!pendingTasksRepository.contains(foregroundTaskInfo.id)) {
			logger.e("Foreground task cannot be rescheduled. There's no saved component")
			return
		}

		pendingTasksRepository.getComponent(foregroundTaskInfo.id)?.let { className ->
			logger.d("className: ${className::class.java}")
			scheduleForeground(Class.forName(className), foregroundTaskInfo)
		}
	}
}
