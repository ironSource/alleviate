package com.rotemati.foregroundsdk.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper

internal class BootCompleteReceiver : BroadcastReceiver() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	override fun onReceive(context: Context, intent: Intent?) {
		val pendingTasksRepository = PendingTasksRepository(context)
		val persistTasks = pendingTasksRepository.foregroundTasks.filter { it.persisted }
		val foregroundTasksScheduler = ForegroundTasksScheduler(context)
		persistTasks.forEach {
			foregroundTasksScheduler.reschedule(it)
		}
	}
}