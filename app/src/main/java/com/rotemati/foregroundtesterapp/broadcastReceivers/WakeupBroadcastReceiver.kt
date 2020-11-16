package com.rotemati.foregroundtesterapp.broadcastReceivers

import android.app.job.JobInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.scheduleForeground
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.notification.notificationDescriptor
import com.rotemati.foregroundtesterapp.R
import com.rotemati.foregroundtesterapp.ReposForegroundObtainer
import java.util.concurrent.TimeUnit

class WakeupBroadcastReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent?) {

		val foregroundTaskInfo = foregroundTaskInfo {
			id = 11200
			networkType = JobInfo.NETWORK_TYPE_ANY
			persisted = true
			minLatencyMillis = TimeUnit.SECONDS.toMillis(0)
			timeout = TimeUnit.SECONDS.toMillis(15)
			notificationDescriptor = notificationDescriptor {
				title = "Rotem"
				body = "Matityahu"
				iconResId = R.drawable.ic_launcher_foreground
			}
			foregroundObtainer = ReposForegroundObtainer()
			rescheduleOnFail = true
			maxRetries = 3
		}
		scheduleForeground(context, foregroundTaskInfo)
	}
}