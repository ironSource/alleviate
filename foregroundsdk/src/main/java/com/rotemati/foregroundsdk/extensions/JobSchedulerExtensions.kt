package com.rotemati.foregroundsdk.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.exceptions.MissingJobInfoException
import com.rotemati.foregroundsdk.foreground.ForegroundService
import com.rotemati.foregroundsdk.jobinfo.ForegroundJobInfo
import com.rotemati.foregroundsdk.jobinfo.PendingJobsRepository
import com.rotemati.foregroundsdk.jobinfo.latencyEpoch
import com.rotemati.foregroundsdk.logger.SDKLogger
import java.util.concurrent.TimeUnit

@Throws(MissingJobInfoException::class)
fun scheduleForeground(
		context: Context,
		foregroundJobInfo: ForegroundJobInfo,
) {

	SDKLogger.d("new retry count: ${foregroundJobInfo.retryCount}")
	val intent = Intent(context, ForegroundService::class.java).apply {
		putExtra(ForegroundService.EXTRA_JOB_ID, foregroundJobInfo.id)
	}

	val foregroundServicePendingIntent = PendingIntent.getForegroundService(context, 0, intent, 0)

	SDKLogger.i(
			"Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(
					foregroundJobInfo.minLatencyMillis
			) + " seconds"
	)

	val pendingJobsRepository = PendingJobsRepository(context)
	SDKLogger.i("Saving jobId: ${foregroundJobInfo.id}")
	pendingJobsRepository.save(foregroundJobInfo)

	context.getAlarmManager().set(
			AlarmManager.RTC_WAKEUP,
			foregroundJobInfo.latencyEpoch(),
			foregroundServicePendingIntent
	)
}