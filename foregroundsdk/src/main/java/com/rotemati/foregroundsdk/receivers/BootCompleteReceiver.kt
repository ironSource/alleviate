package com.rotemati.foregroundsdk.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.PendingTasksRepository
import com.rotemati.foregroundsdk.logger.SDKLogger

internal class BootCompleteReceiver : BroadcastReceiver() {

	private val foregroundTasksScheduler = ForegroundTasksScheduler()

	override fun onReceive(context: Context, intent: Intent?) {
		SDKLogger.logMethod()
		val pendingTasksRepository = PendingTasksRepository(context)
		val persistTasks = pendingTasksRepository.pendingForegroundTasks.filter { it.persisted }
		persistTasks.forEach {
			foregroundTasksScheduler.scheduleForeground(context, it)
		}
	}
}