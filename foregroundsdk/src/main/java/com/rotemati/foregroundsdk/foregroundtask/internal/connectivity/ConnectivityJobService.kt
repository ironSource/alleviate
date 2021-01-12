package com.rotemati.foregroundsdk.foregroundtask.internal.connectivity

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getJobScheduler
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import kotlin.concurrent.thread

private const val JOB_SERVICE_ID = 12
private const val FOREGROUND_TASK_ID = "FOREGROUND_TASK_ID"

internal class ConnectivityJobService : JobService() {

	private lateinit var foregroundTasksSchedulerWrapper: ForegroundTasksSchedulerWrapper
	private lateinit var pendingTasksRepository: PendingTasksRepository

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	override fun onCreate() {
		foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
		pendingTasksRepository = PendingTasksRepository()
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		thread {
			params?.extras?.getInt(FOREGROUND_TASK_ID)?.let { taskId ->
				pendingTasksRepository.getById(taskId)?.let { nonNullTask ->
					logger.d("Rescheduling $nonNullTask")
					val newTask = TaskInfoSpec(
							foregroundTaskInfo = ForegroundTaskInfo(
									id = nonNullTask.foregroundTaskInfo.id,
									networkType = nonNullTask.foregroundTaskInfo.networkType,
									persisted = nonNullTask.foregroundTaskInfo.persisted,
									minLatencyMillis = nonNullTask.foregroundTaskInfo.minLatencyMillis,
									timeoutMillis = nonNullTask.foregroundTaskInfo.timeoutMillis,
									retryCount = nonNullTask.foregroundTaskInfo.retryCount,
									runImmediately = true
							),
							componentName = nonNullTask.componentName
					)
					foregroundTasksSchedulerWrapper.reschedule(newTask)
				}
			}
		}
		return false
	}

	override fun onStopJob(params: JobParameters?) = false

	companion object {
		private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
		private val converter = NetworkTypeToJobSchedulerConverter()

		fun schedule(
				context: Context,
				taskInfo: ForegroundTaskInfo,
		) {
			val bundle = PersistableBundle().apply {
				putInt(FOREGROUND_TASK_ID, taskInfo.id)
			}
			val jobInfoBuilder = JobInfo.Builder(
					JOB_SERVICE_ID,
					ComponentName(context.packageName, ConnectivityJobService::class.java.name)
			).setPersisted(taskInfo.persisted).setRequiredNetworkType(converter.convert(taskInfo.networkType))
					.setExtras(bundle)
			val result = context.getJobScheduler().schedule(jobInfoBuilder.build())
			if (result == JobScheduler.RESULT_FAILURE) {
				logger.e("Job scheduling has failed")
			}
		}
	}
}