package com.rotemati.foregroundtesterapp.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import com.rotemati.foregroundtesterapp.services.ReposForegroundService
import java.util.concurrent.TimeUnit

class WakeupBroadcastReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent?) {
		val foregroundTaskInfo = foregroundTaskInfo {
			id = 12341
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