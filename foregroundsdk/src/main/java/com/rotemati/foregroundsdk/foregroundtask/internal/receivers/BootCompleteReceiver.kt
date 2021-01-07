package com.rotemati.foregroundsdk.foregroundtask.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository

internal class BootCompleteReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent?) {
		val pendingTasksRepository = PendingTasksRepository(context)
		pendingTasksRepository.getAll { tasks ->
			val persistedTasks = tasks.filter { it.persisted }
			val foregroundTasksScheduler = ForegroundTasksSchedulerWrapper(context)
			persistedTasks.forEach {
				foregroundTasksScheduler.reschedule(it)
			}
		}
	}
}