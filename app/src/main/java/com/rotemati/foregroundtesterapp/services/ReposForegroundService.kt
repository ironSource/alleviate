package com.rotemati.foregroundtesterapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundsdk.foregroundtask.external.reschedulepolicy.RetryPolicy
import com.rotemati.foregroundsdk.foregroundtask.external.services.ForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.result.Result
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.logger.TesterAppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService

class ReposForegroundService : ForegroundTaskService() {

	override fun getNotification(): Notification {
		// create channel
		val channelId = resources.getString(R.string.my_channel)
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			val notificationChannel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
			notificationChannel.setSound(null, null)
			val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(notificationChannel)
		}

		return NotificationCompat.Builder(this, channelId)
				.setContentTitle(resources.getString(R.string.my_title))
				.setContentText(resources.getString(R.string.my_body))
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.build()
	}

	override fun doWork(): Result {
		return try {
			val repos = GitHubRepo(getNetworkService()).getRepos().execute()
			TesterAppLogger.i("${repos.body()?.size} repos fetched")
			Result.Success
		} catch (e: Exception) {
			e.message?.let { TesterAppLogger.e(it) }
			TesterAppLogger.i("retryCount: ${foregroundTaskInfo.retryCount}")
			if (foregroundTaskInfo.retryCount >= 3) {
				Result.Failed
			} else {
				Result.Reschedule(RetryPolicy.Linear)
			}
		}
	}

	override fun onTimeout(): Result {
		TesterAppLogger.d("onTimeout")
		TesterAppLogger.i("retryCount: ${foregroundTaskInfo.retryCount}")
		return if (foregroundTaskInfo.retryCount >= 3) {
			Result.Failed
		} else {
			Result.Reschedule(RetryPolicy.Linear)
		}
	}
}