package com.rotemati.foregroundsdk.external.services

import android.app.Notification
import com.rotemati.foregroundsdk.external.ForegroundSDK
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import java.util.*

abstract class ForegroundTaskService : BaseForegroundTaskService() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val timer: Timer = Timer()

	abstract override fun getNotification(): Notification

	abstract fun doWork(): Result

	abstract fun onTimeout(): Result

	override fun startWork(): Result {
		val timerTask = object : TimerTask() {
			override fun run() {
				timer.cancel()
				onTimeout()
			}
		}
		timer.schedule(timerTask, foregroundTaskInfo.timeoutMillis)
		return doWork().also {
			logger.d("cancel timer")
			timer.cancel()
		}
	}
}