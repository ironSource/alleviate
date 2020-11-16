package com.rotemati.foregroundsdk.network

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.rotemati.foregroundsdk.extensions.getJobScheduler
import com.rotemati.foregroundsdk.foregroundtask.scheduleForeground
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.PendingTasksRepository
import com.rotemati.foregroundsdk.logger.SDKLogger

private const val JOB_SERVICE_ID = 12
private const val FOREGROUND_TASK_ID = "FOREGROUND_TASK_ID"

class ConnectivityJobService : JobService() {

	private lateinit var pendingTasksRepository: PendingTasksRepository
	override fun onCreate() {
		pendingTasksRepository = PendingTasksRepository(this)
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		params?.extras?.getInt(FOREGROUND_TASK_ID)?.let { taskId ->
			val taskInfo = pendingTasksRepository.pendingForegroundTasks.find { it.id == taskId }
			taskInfo?.let {
				scheduleForeground(this, it)
			}
		}
		return false
	}

	override fun onStopJob(params: JobParameters?) = false

	companion object {
		fun schedule(context: Context, persisted: Boolean, networkType: Int, id: Int) {
			val bundle = PersistableBundle().apply {
				putInt(FOREGROUND_TASK_ID, id)
			}
			val jobInfoBuilder = JobInfo.Builder(JOB_SERVICE_ID, ComponentName(context.packageName, ConnectivityJobService::class.java.name)).setPersisted(persisted).setRequiredNetworkType(networkType).setExtras(bundle)
			val result = context.getJobScheduler().schedule(jobInfoBuilder.build())
			if (result == JobScheduler.RESULT_FAILURE) {
				SDKLogger.e("Job scheduling has failed")
			}
		}
	}
}