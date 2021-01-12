package com.rotemati.foregroundsdk.foregroundtask.external.scheduler

import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK.context
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksSchedulerPost26
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksSchedulerPre26
import kotlin.concurrent.thread

//think about testing - mock the scheduler
class ForegroundTasksSchedulerWrapper {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val pendingTasksRepository = PendingTasksRepository()
	private val foregroundTasksScheduler: ForegroundTasksScheduler = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
		ForegroundTasksSchedulerPost26(context)
	} else {
		ForegroundTasksSchedulerPre26(context)
	}

	fun scheduleForegroundTask(
			className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo
	) {
		val taskInfoSpec = TaskInfoSpec(foregroundTaskInfo, className.name)

//		if (className.superclass !is BaseForegroundTaskService) {
//			SDKLogger.e("Component should extend from CoroutineForegroundTaskService or ForegroundTaskService")
//			return
//		}

		thread {
			logger.i("Saving taskId: ${foregroundTaskInfo.id}")
			pendingTasksRepository.save(taskInfoSpec)
			foregroundTasksScheduler.schedule(taskInfoSpec)
		}
	}

	internal fun reschedule(taskInfoSpec: TaskInfoSpec) {
		logger.i("Rescheduling task id: ${taskInfoSpec.foregroundTaskInfo.id}")
		pendingTasksRepository.save(taskInfoSpec)
		foregroundTasksScheduler.schedule(taskInfoSpec)
	}
}
