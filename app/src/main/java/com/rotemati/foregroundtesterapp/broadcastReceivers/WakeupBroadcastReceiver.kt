package com.rotemati.foregroundtesterapp.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.workmanagertest.WorkmnagerScheduler
import com.rotemati.foregroundtesterapp.services.CoroutineRepoForegroundService
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
		}
		ForegroundTasksScheduler(context).scheduleForeground(
				CoroutineRepoForegroundService::class.java,
				foregroundTaskInfo
		)

		//////////////////////////////////////////////////////////

		val workManagerTaskInfo = foregroundTaskInfo {
			id = 2121212
			networkType = NetworkType.Any
			persisted = true
			minLatencyMillis = TimeUnit.SECONDS.toMillis(0)
			timeoutMillis = TimeUnit.SECONDS.toMillis(15)
		}

//		WorkmnagerScheduler().schedule(context, workManagerTaskInfo)
	}
}