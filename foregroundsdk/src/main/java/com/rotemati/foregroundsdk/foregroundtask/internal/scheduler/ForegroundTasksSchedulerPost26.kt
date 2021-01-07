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

private const val STANDBY_BUCKET_NEVER = 50

class ForegroundTasksSchedulerPost26(private val context: Context) : ForegroundTasksScheduler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	@RequiresApi(Build.VERSION_CODES.O)
	override fun schedule(className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			if (context.getStandbyBucket() == STANDBY_BUCKET_NEVER) {
				logger.d("App is in 'Never' bucket")
				if (foregroundTaskInfo.minLatencyMillis == 0L) {

				}
			}
		}

		val intent = Intent(context, className).apply {
			putExtra(BaseForegroundTaskService.EXTRA_TASK_ID, foregroundTaskInfo.id)
		}
		val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)

		context.getAlarmManager().set(
				AlarmManager.RTC_WAKEUP,
				foregroundTaskInfo.latencyEpoch(),
				foregroundServicePendingIntent)
	}

	override fun alreadyScheduled(foregroundTaskInfo: ForegroundTaskInfo): Boolean {
		//todo check if it's right
		return PendingIntent.getBroadcast(
				context, 0,
				Intent("com.ironsource.scheduleForeground"),
				PendingIntent.FLAG_NO_CREATE
		) != null
	}

	override fun cancel(foregroundTaskInfo: ForegroundTaskInfo) {
		TODO("Not yet implemented")
	}
}