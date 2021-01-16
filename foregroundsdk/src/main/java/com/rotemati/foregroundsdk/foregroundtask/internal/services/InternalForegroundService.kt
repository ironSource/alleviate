package com.rotemati.foregroundsdk.foregroundtask.internal.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksSchedulerPost26.Companion.ACTION_SHOW_NOTIFICATION
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksSchedulerPost26.Companion.EXTRA_NOTIFICATION

class InternalForegroundService : Service() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val notificationId = InternalForegroundService::class.java.name.hashCode()

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		try {
			if (intent != null) {
				val action = intent.action
				logger.d("action from ForegroundProvider.InternalForegroundService: $action")
				if (ACTION_SHOW_NOTIFICATION == action) {

				}
				val notification = intent.getParcelableExtra<Notification>(EXTRA_NOTIFICATION)
				startForeground(notificationId, notification)
			}
		} catch (ex: Exception) {
			ex.message?.let { logger.e(it) }
		}
		return START_NOT_STICKY
	}

	override fun onBind(intent: Intent?) = null
}