package com.rotemati.foregroundsdk.foregroundtask.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class MyPackageReplacedReceiver : BroadcastReceiver() {

	private val foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
	private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
	private val pendingTasksRepository = PendingTasksRepository()

	override fun onReceive(context: Context, intent: Intent?) {
		executorService.submit {
			pendingTasksRepository.getAll().forEach { nonNullTaskInfoSpec ->
				foregroundTasksSchedulerWrapper.scheduleForegroundTask(
						Class.forName(nonNullTaskInfoSpec.componentName),
						nonNullTaskInfoSpec.foregroundTaskInfo
				)
			}
		}
	}
}