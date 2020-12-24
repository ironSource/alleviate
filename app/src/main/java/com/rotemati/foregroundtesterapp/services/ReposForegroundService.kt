package com.rotemati.foregroundtesterapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundsdk.extensions.getNotificationManager
import com.rotemati.foregroundsdk.foregroundtask.services.ForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.logger.TesterAppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService

class ReposForegroundService : ForegroundTaskService() {

	override fun getNotification(): Notification {
		// create channel
		val channelId = "kotlin channel"
		val notificationChannel =
				NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
		notificationChannel.setSound(null, null)
		this.getNotificationManager().createNotificationChannel(notificationChannel)

		return NotificationCompat.Builder(this, channelId)
				.setContentTitle("ReposForegroundService Title")
				.setContentText("ReposForegroundService Text")
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.build()
	}

	override fun doWork(): Result {
		return try {
			val futureRepos = GitHubRepo(getNetworkService()).getRepos()
			TesterAppLogger.i("${futureRepos.get().size} repos fetched")
			Result.Success
		} catch (e: Exception) {
			e.message?.let { TesterAppLogger.e(it) }
			TesterAppLogger.i("retryCount: ${getForegroundTaskInfo().retryCount}")
			if (getForegroundTaskInfo().retryCount >= 3) {
				Result.Failed()
			} else {
				Result.Reschedule
			}
		}
	}
}