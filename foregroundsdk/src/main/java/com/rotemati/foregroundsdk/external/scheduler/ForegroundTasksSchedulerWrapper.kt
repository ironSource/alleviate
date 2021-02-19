package com.rotemati.foregroundsdk.external.scheduler

import android.app.AlarmManager
import android.os.Build
import com.rotemati.foregroundsdk.external.ForegroundSdk
import com.rotemati.foregroundsdk.external.ForegroundSdk.context
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.internal.extensions.getAlarmManager
import com.rotemati.foregroundsdk.internal.extensions.toDateFormat
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec
import com.rotemati.foregroundsdk.internal.scheduler.ForegroundTasksPendingIntentFactory
import com.rotemati.foregroundsdk.internal.scheduler.ForegroundTasksPendingIntentFactoryPost26
import com.rotemati.foregroundsdk.internal.scheduler.ForegroundTasksPendingIntentFactoryPre26

class ForegroundTasksSchedulerWrapper {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSdk.logger)
	private val pendingTasksRepository = PendingTasksRepository(context)
	private val mForegroundTasksPendingIntentFactory: ForegroundTasksPendingIntentFactory =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				ForegroundTasksPendingIntentFactoryPost26(context)
			} else {
				ForegroundTasksPendingIntentFactoryPre26(context)
			}

	fun scheduleForegroundTask(className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo) {
		val newForegroundTaskInfo = foregroundTaskInfo.copy(
				triggerTime = calculateTriggerTime(foregroundTaskInfo)
		)
		logger.i("Scheduling task to run at ${newForegroundTaskInfo.triggerTime.toDateFormat()}")
		val taskInfoSpec = TaskInfoSpec(newForegroundTaskInfo, className.name)
		pendingTasksRepository.insert(taskInfoSpec)
		context.getAlarmManager().set(
				AlarmManager.RTC_WAKEUP,
				newForegroundTaskInfo.triggerTime,
				mForegroundTasksPendingIntentFactory.createSchedulePendingIntent(taskInfoSpec)
		)
	}

	private fun calculateTriggerTime(foregroundTaskInfo: ForegroundTaskInfo): Long {
		return if (foregroundTaskInfo.isScheduled()) {
			foregroundTaskInfo.triggerTime
		} else {
			System.currentTimeMillis() + foregroundTaskInfo.minLatencyMillis
		}
	}

	fun cancel(taskId: Int) {
		pendingTasksRepository.getTaskInfo(taskId)?.let { nonNullTaskSpec ->
			logger.i("cancel task $taskId")
			context.getAlarmManager().cancel(
					mForegroundTasksPendingIntentFactory.createCancelPendingIntent(nonNullTaskSpec)
			)
			pendingTasksRepository.delete(nonNullTaskSpec.foregroundTaskInfo.id)
		}
	}
}
