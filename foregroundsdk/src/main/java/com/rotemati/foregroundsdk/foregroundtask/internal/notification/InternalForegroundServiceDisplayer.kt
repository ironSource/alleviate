package com.rotemati.foregroundsdk.foregroundtask.internal.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.services.InternalForegroundService

class InternalForegroundServiceDisplayer(
		private val context: Context
) {
	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	@RequiresApi(Build.VERSION_CODES.O)
	fun show() {
		try {
			val intent = Intent(context, InternalForegroundService::class.java).apply {
				action = ForegroundTasksSchedulerWrapper.ACTION_SHOW_NOTIFICATION
				putExtra(ForegroundTasksSchedulerWrapper.EXTRA_NOTIFICATION, DefaultNotificationCreator().create(ForegroundSDK.context))
			}
			context.startForegroundService(intent)
		} catch (e: Exception) {
			e.message?.let { logger.e(it) }
		}
	}

	fun dismiss() {
		try {
			val intent = Intent(context, InternalForegroundService::class.java)
			context.stopService(intent)
		} catch (e: Exception) {
			e.message?.let { logger.e(it) }
		}
	}
}