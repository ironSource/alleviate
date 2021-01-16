package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.services.BaseForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getAlarmManager
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getStandbyBucket
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.notification.DefaultNotificationCreator
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import com.rotemati.foregroundsdk.foregroundtask.internal.services.InternalForegroundService
import java.util.concurrent.TimeUnit

private const val RUN_IMMEDIATELY = 0L
private const val STANDBY_BUCKET_NEVER = 50

internal class ForegroundTasksSchedulerPost26(
		private val context: Context
) : ForegroundTasksScheduler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val defaultNotificationCreator = DefaultNotificationCreator()
	private var foregroundServiceStartTime = 0L
	private val handler = Handler()

	@RequiresApi(Build.VERSION_CODES.O)
	override fun schedule(taskInfoSpec: TaskInfoSpec) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			if (context.getStandbyBucket() == STANDBY_BUCKET_NEVER) {
				logger.d("App is in 'Never' bucket - starting foreground service")
				val intent = Intent(context, InternalForegroundService::class.java)
				intent.action = ACTION_SHOW_NOTIFICATION
				intent.putExtra(EXTRA_NOTIFICATION, defaultNotificationCreator.create(context))
				context.startForegroundService(intent)
				foregroundServiceStartTime = System.currentTimeMillis();
				onInternalForegroundStarted()
			}
		}

		val intent = Intent(context, Class.forName(taskInfoSpec.componentName)).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
		}
		val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)
		logger.i(
				"Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(
						taskInfoSpec.foregroundTaskInfo.minLatencyMillis
				) + " seconds"
		)

		val triggerAtMillis = if (taskInfoSpec.foregroundTaskInfo.shouldRunImmediately()) {
			RUN_IMMEDIATELY
		} else {
			taskInfoSpec.foregroundTaskInfo.latencyEpoch()
		}
		context.getAlarmManager().set(
				AlarmManager.RTC_WAKEUP,
				triggerAtMillis,
				foregroundServicePendingIntent
		)
	}

	@RequiresApi(Build.VERSION_CODES.P)
	private fun onInternalForegroundStarted() {
		if (isPollingTimeoutPassed()) {
			finishBucketPolling()
		} else {
			scheduleBucketPolling()
		}
	}

	@RequiresApi(Build.VERSION_CODES.P)
	private fun scheduleBucketPolling() {
		logger.i("scheduleBucketPolling")
		handler.postDelayed({
			verifyBucket()
		}, 4000)
	}

	@RequiresApi(Build.VERSION_CODES.P)
	private fun verifyBucket() {
		logger.i("verifyBucket")
		val standbyBucket = context.getStandbyBucket()
		if (standbyBucket == STANDBY_BUCKET_NEVER) {
			logger.i("standbyBucket: $standbyBucket")
			scheduleBucketPolling()
		} else {
			finishBucketPolling()
		}
	}

	private fun finishBucketPolling() {
		logger.i("Finish bucket polling")
		val intent = Intent(context, InternalForegroundService::class.java)
		context.stopService(intent)
	}

	private fun isPollingTimeoutPassed(): Boolean {
		return System.currentTimeMillis() - foregroundServiceStartTime >= TimeUnit.MINUTES.toMillis(1)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	override fun cancel(taskInfoSpec: TaskInfoSpec) {
		val intent = Intent(context, Class.forName(taskInfoSpec.componentName)).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
		}
		val foregroundServicePendingIntent =
				PendingIntent.getForegroundService(context, 0, intent, 0)
		context.getAlarmManager().cancel(foregroundServicePendingIntent)
	}

	companion object {
		const val ACTION_SHOW_NOTIFICATION = "com.rotemati.foregroundsdk.ACTION_SHOW_NOTIFICATION"
		const val EXTRA_NOTIFICATION = "com.rotemati.foregroundsdk.EXTRA_NOTIFICATION"
	}
}