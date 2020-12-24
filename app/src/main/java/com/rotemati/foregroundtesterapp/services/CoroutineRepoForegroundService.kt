package com.rotemati.foregroundtesterapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.extensions.getNotificationManager
import com.rotemati.foregroundsdk.foregroundtask.services.CoroutineForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.logger.TesterAppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService
import kotlinx.coroutines.delay

class CoroutineRepoForegroundService : CoroutineForegroundTaskService() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	override fun getNotification(): Notification {
		// create channel
		val channelId = "Coroutine channel"
		val notificationChannel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
		notificationChannel.setSound(null, null)
		this.getNotificationManager().createNotificationChannel(notificationChannel)

		return NotificationCompat.Builder(this, channelId)
				.setContentTitle("ForegroundTasksScheduler Title")
				.setContentText("ForegroundTasksScheduler Text")
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.build()
	}

	override suspend fun doWork(): Result {
		return try {
			delay(5000)
			val repos = GitHubRepo(getNetworkService()).getReposSuspend()
			logger.i("${repos.size} repos fetched")
			Result.Success
		} catch (e: Exception) {
			e.message?.let { TesterAppLogger.e(it) }
			logger.i("retryCount: ${getForegroundTaskInfo().retryCount}")
			if (getForegroundTaskInfo().retryCount >= 3) {
				Result.Failed()
			} else {
				Result.Reschedule
			}
		}
	}
}