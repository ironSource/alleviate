package com.rotemati.foregroundtesterapp.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry.RetryPolicy
import com.rotemati.foregroundsdk.workmanagertest.WorkmnagerScheduler
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.services.ReposForegroundService
import java.util.concurrent.TimeUnit

class WakeupBroadcastReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent?) {
		val foregroundTaskInfo = foregroundTaskInfo {
			id = 11200
			networkType = NetworkType.Any
			persisted = true
			minLatencyMillis = TimeUnit.SECONDS.toMillis(0)
			timeoutMillis = TimeUnit.SECONDS.toMillis(15)
			notification = NotificationCompat.Builder(context, "kotlin channel")
					.setContentTitle("ForegroundTasksScheduler Title")
					.setContentText("ForegroundTasksScheduler Text")
					.setSmallIcon(R.drawable.ic_launcher_foreground)
					.build()
			retryPolicy = RetryPolicy.Retry(true, 3)
		}
		ForegroundTasksScheduler(context).scheduleForeground(
				ReposForegroundService::class.java,
				foregroundTaskInfo
		)

		//////////////////////////////////////////////////////////

		val workManagerTaskInfo = foregroundTaskInfo {
			id = 2121212
			networkType = NetworkType.Any
			persisted = true
			minLatencyMillis = TimeUnit.SECONDS.toMillis(0)
			timeoutMillis = TimeUnit.SECONDS.toMillis(15)
			notification = NotificationCompat.Builder(context, "WorkManager channel")
					.setContentTitle("WorkManager Title")
					.setContentText("WorkManager Text")
					.setSmallIcon(R.drawable.ic_launcher_foreground)
					.build()
			retryPolicy = RetryPolicy.Retry(true, 3)
		}

		WorkmnagerScheduler().schedule(context, workManagerTaskInfo)
	}
}