package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.services.BaseForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.connectivity.NetworkTypeToJobSchedulerConverter
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getJobScheduler
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec

internal class ForegroundTasksSchedulerPre26(
		private val context: Context
) : ForegroundTasksScheduler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val converter = NetworkTypeToJobSchedulerConverter()

	override fun schedule(taskInfoSpec: TaskInfoSpec) {
		//todo check if it's working
		val bundle = PersistableBundle().apply {
			putInt(BaseForegroundTaskService.EXTRA_TASK_ID, taskInfoSpec.foregroundTaskInfo.id)
		}
		val jobInfoBuilder = JobInfo.Builder(
				taskInfoSpec.foregroundTaskInfo.id,
				ComponentName(context.packageName, taskInfoSpec.componentName)
		).setPersisted(taskInfoSpec.foregroundTaskInfo.persisted).setRequiredNetworkType(converter.convert(taskInfoSpec.foregroundTaskInfo.networkType))
				.setExtras(bundle)
		val result = context.getJobScheduler().schedule(jobInfoBuilder.build())
		if (result == JobScheduler.RESULT_FAILURE) {
			logger.e("Job scheduling has failed")
		}
	}

	override fun cancel(foregroundTaskInfo: ForegroundTaskInfo) {
		context.getJobScheduler().cancel(foregroundTaskInfo.id)
	}
}