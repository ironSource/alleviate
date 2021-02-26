package com.rotemati.foregroundsdk.internal.connectivity

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.rotemati.foregroundsdk.external.ForegroundSdk
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.internal.extensions.getJobScheduler
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.internal.repositories.PendingTasksRepository

private const val FOREGROUND_TASK_ID = "FOREGROUND_TASK_ID"

internal class ConnectivityJobService : JobService() {

	private lateinit var foregroundTasksSchedulerWrapper: ForegroundTasksSchedulerWrapper
	private lateinit var pendingTasksRepository: PendingTasksRepository

	override fun onCreate() {
		foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
		pendingTasksRepository = PendingTasksRepository(this)
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		logger.d("onStartJob")
		params?.extras?.getInt(FOREGROUND_TASK_ID)?.let { taskId ->
			pendingTasksRepository.getTaskInfo(taskId)?.let { nonNullTask ->
				foregroundTasksSchedulerWrapper.scheduleForegroundTask(Class.forName(nonNullTask.componentName), nonNullTask.foregroundTaskInfo)
			}
		}
		return false
	}

	override fun onStopJob(params: JobParameters?) = false

	companion object {
		private val logger: ForegroundLogger = LoggerWrapper(ForegroundSdk.logger)
		private val converter = NetworkTypeToJobSchedulerConverter()

		fun schedule(
				context: Context,
				taskInfo: ForegroundTaskInfo,
		) {
			val bundle = PersistableBundle().apply {
				putInt(FOREGROUND_TASK_ID, taskInfo.id)
			}
			val jobInfoBuilder = JobInfo.Builder(
					taskInfo.id,
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