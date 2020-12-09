package com.rotemati.foregroundsdk.connectivity

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.rotemati.foregroundsdk.extensions.getJobScheduler
import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.logger.SDKLogger

private const val JOB_SERVICE_ID = 12
private const val FOREGROUND_TASK_ID = "FOREGROUND_TASK_ID"
private const val FOREGROUND_TASK_COMPONENT = "FOREGROUND_TASK_COMPONENT"

internal class ConnectivityJobService : JobService() {

	private lateinit var foregroundTasksScheduler: ForegroundTasksScheduler
	private lateinit var pendingTasksRepository: PendingTasksRepository

	override fun onCreate() {
		foregroundTasksScheduler = ForegroundTasksScheduler(this)
		pendingTasksRepository = PendingTasksRepository(this)
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		SDKLogger.logMethod()
		params?.extras?.getInt(FOREGROUND_TASK_ID)?.let { taskId ->
			val taskInfo = pendingTasksRepository.pendingForegroundTasks.find { it.id == taskId } ?: return false
			val className = params.extras.getString(FOREGROUND_TASK_COMPONENT) ?: return false
			val cls = Class.forName(className)
			foregroundTasksScheduler.scheduleForeground(cls, taskInfo)
		}
		return false
	}

	override fun onStopJob(params: JobParameters?) = false

	companion object {
		fun schedule(
				context: Context,
				javaClass: Class<*>,
				persisted: Boolean,
				networkType: NetworkType,
				id: Int
		) {
			val bundle = PersistableBundle().apply {
				putInt(FOREGROUND_TASK_ID, id)
				putString(FOREGROUND_TASK_COMPONENT, javaClass.simpleName)
			}
			val jobInfoBuilder = JobInfo.Builder(
					JOB_SERVICE_ID,
					ComponentName(context.packageName, ConnectivityJobService::class.java.name)
			).setPersisted(persisted).setRequiredNetworkType(networkType.JobInfoNetworkType).setExtras(bundle)
			val result = context.getJobScheduler().schedule(jobInfoBuilder.build())
			if (result == JobScheduler.RESULT_FAILURE) {
				SDKLogger.e("Job scheduling has failed")
			}
		}
	}
}