package com.rotemati.foregroundsdk.foregroundtask.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.UpdatedTriggerTimeCalculator
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class MyPackageReplacedReceiver : BroadcastReceiver() {

	private val foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
	private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
	private val pendingTasksRepository = PendingTasksRepository()
	private val updatedTriggerTimeCalculator = UpdatedTriggerTimeCalculator(pendingTasksRepository)

	override fun onReceive(context: Context, intent: Intent?) {
		executorService.submit {
			pendingTasksRepository.getAll().forEach { nonNullTaskInfoSpec ->
				val id = nonNullTaskInfoSpec.foregroundTaskInfo.id
				val newDelayTime = updatedTriggerTimeCalculator.calculate(nonNullTaskInfoSpec)
				val newTaskInfoSpec = TaskInfoSpec(
						componentName = nonNullTaskInfoSpec.componentName,
						foregroundTaskInfo = foregroundTaskInfo(id) {
							networkType = nonNullTaskInfoSpec.foregroundTaskInfo.networkType
							persisted = nonNullTaskInfoSpec.foregroundTaskInfo.persisted
							timeoutMillis = nonNullTaskInfoSpec.foregroundTaskInfo.timeoutMillis
							minLatencyMillis = newDelayTime
						}
				)
				foregroundTasksSchedulerWrapper.scheduleForegroundTask(Class.forName(newTaskInfoSpec.componentName), newTaskInfoSpec.foregroundTaskInfo)
			}
		}
	}
}