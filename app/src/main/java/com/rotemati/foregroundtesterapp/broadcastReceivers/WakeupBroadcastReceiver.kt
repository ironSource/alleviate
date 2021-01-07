package com.rotemati.foregroundtesterapp.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType
import com.rotemati.foregroundtesterapp.services.ReposForegroundService
import java.util.concurrent.TimeUnit

class WakeupBroadcastReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent?) {
		val foregroundTaskInfo = foregroundTaskInfo(11200) {
			networkType = NetworkType.Any
			persisted = true
			minLatencyMillis = TimeUnit.SECONDS.toMillis(0)
			timeoutMillis = TimeUnit.SECONDS.toMillis(15)
		}
		ForegroundTasksSchedulerWrapper().scheduleForegroundTask(
				ReposForegroundService::class.java,
				foregroundTaskInfo
		)
	}
}