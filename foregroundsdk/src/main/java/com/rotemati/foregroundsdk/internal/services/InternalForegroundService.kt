package com.rotemati.foregroundsdk.internal.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.external.ForegroundSDK
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper.Companion.ACTION_SHOW_NOTIFICATION
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper.Companion.EXTRA_NOTIFICATION
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper

internal class InternalForegroundService : Service() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val notificationId = InternalForegroundService::class.java.name.hashCode()

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		try {
			if (intent != null) {
				if (ACTION_SHOW_NOTIFICATION == intent.action) {
					val notification = intent.getParcelableExtra<Notification>(EXTRA_NOTIFICATION)
					startForeground(notificationId, notification)
				}
			}
		} catch (ex: Exception) {
			ex.message?.let { logger.e(it) }
		}
		return START_NOT_STICKY
	}

	override fun onBind(intent: Intent?): Nothing? = null
}