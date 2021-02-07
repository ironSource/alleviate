package com.rotemati.foregroundsdk.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.external.ForegroundSdk.context
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.internal.repositories.PendingTasksRepository

internal class BootCompleteReceiver : BroadcastReceiver() {

	private val foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
	private val pendingTasksRepository = PendingTasksRepository(context)

	override fun onReceive(context: Context, intent: Intent?) {
		pendingTasksRepository.getAll().filter {
			it.foregroundTaskInfo.persisted
		}.forEach { nonNullTaskInfoSpec ->
			foregroundTasksSchedulerWrapper.scheduleForegroundTask(
					Class.forName(nonNullTaskInfoSpec.componentName),
					nonNullTaskInfoSpec.foregroundTaskInfo
			)
		}
	}
}