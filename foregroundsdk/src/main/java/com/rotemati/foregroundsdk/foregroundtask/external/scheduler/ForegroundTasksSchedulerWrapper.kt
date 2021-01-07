package com.rotemati.foregroundsdk.foregroundtask.external.scheduler

import android.content.Context
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksSchedulerPost26
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksSchedulerPre26
import java.util.concurrent.TimeUnit

//think about testing - mock the scheduler
class ForegroundTasksSchedulerWrapper(private val context: Context) {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val pendingTasksRepository = PendingTasksRepository(context)
	private val foregroundTasksScheduler: ForegroundTasksScheduler = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
		ForegroundTasksSchedulerPost26(context)
	} else {
		ForegroundTasksSchedulerPre26(context)
	}

	fun scheduleForegroundTask(
			className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo,
	) {
		if (foregroundTasksScheduler.alreadyScheduled(foregroundTaskInfo)) {
			logger.i("Foreground task already scheduled")
			return
		}
//		if (className.superclass !is BaseForegroundTaskService) {
//			SDKLogger.e("Component should extend from CoroutineForegroundTaskService or ForegroundTaskService")
//			return
//		}
		logger.i("Saving taskId: ${foregroundTaskInfo.id}")
		pendingTasksRepository.save(TaskInfoSpec(foregroundTaskInfo, className.toString()))

		logger.i("Scheduling ForegroundJobService to run in " + TimeUnit.MILLISECONDS.toSeconds(foregroundTaskInfo.minLatencyMillis) + " seconds")

		foregroundTasksScheduler.schedule(className, foregroundTaskInfo)
	}

	internal fun reschedule(foregroundTaskInfo: ForegroundTaskInfo) {
		val taskInfoSpec = pendingTasksRepository.getTaskInfoSpec(foregroundTaskInfo.id)
		if (taskInfoSpec == null) {
			logger.e("Foreground task cannot be rescheduled. It's not contained in the db")
			return
		}
		logger.i("Rescheduling task id: ${foregroundTaskInfo.id}")
		foregroundTasksScheduler.schedule(Class.forName(taskInfoSpec.componentName), foregroundTaskInfo)
		scheduleForegroundTask(Class.forName(taskInfoSpec.componentName), foregroundTaskInfo)
	}
}
