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
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getJobScheduler
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper

class ForegroundTasksSchedulerPre26(private val context: Context) : ForegroundTasksScheduler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	override fun schedule(className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo) {
		//todo check if it's working
		val bundle = PersistableBundle().apply {
			putInt(BaseForegroundTaskService.EXTRA_TASK_ID, foregroundTaskInfo.id)
		}
		val jobInfoBuilder = JobInfo.Builder(
				foregroundTaskInfo.id,
				ComponentName(context.packageName, className.name)
		).setPersisted(foregroundTaskInfo.persisted).setRequiredNetworkType(foregroundTaskInfo.networkType.ordinal)
				.setExtras(bundle)
		val result = context.getJobScheduler().schedule(jobInfoBuilder.build())
		if (result == JobScheduler.RESULT_FAILURE) {
			logger.e("Job scheduling has failed")
		}
	}

	override fun alreadyScheduled(foregroundTaskInfo: ForegroundTaskInfo): Boolean {
		TODO("Not yet implemented")
	}

	override fun cancel(foregroundTaskInfo: ForegroundTaskInfo) {
		TODO("Not yet implemented")
	}
}