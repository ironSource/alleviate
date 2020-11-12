package com.rotemati.foregroundsdk.network

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.rotemati.foregroundsdk.extensions.getJobScheduler
import com.rotemati.foregroundsdk.extensions.scheduleForeground
import com.rotemati.foregroundsdk.jobinfo.PendingJobsRepository
import com.rotemati.foregroundsdk.logger.SDKLogger

private const val JOB_SERVICE_ID = 12
private const val FOREGROUND_JOB_ID = "FOREGROUND_JOB_ID"

class ConnectivityJobService : JobService() {

	private lateinit var pendingJobsRepository: PendingJobsRepository
	override fun onCreate() {
		pendingJobsRepository = PendingJobsRepository(this)
	}

	override fun onStartJob(params: JobParameters?): Boolean {
		params?.extras?.getInt(FOREGROUND_JOB_ID)?.let { jobId ->
			val jobInfo = pendingJobsRepository.pendingForegroundJobs.find { it.id == jobId }
			jobInfo?.let {
				scheduleForeground(this, it)
			}
		}
		return false
	}

	override fun onStopJob(params: JobParameters?) = false

	companion object {
		fun schedule(context: Context, persisted: Boolean, networkType: Int, id: Int) {
			val bundle = PersistableBundle().apply {
				putInt(FOREGROUND_JOB_ID, id)
			}
			val jobInfoBuilder = JobInfo.Builder(JOB_SERVICE_ID, ComponentName(context.packageName, ConnectivityJobService::class.java.name)).setPersisted(persisted).setRequiredNetworkType(networkType).setExtras(bundle)
			val result = context.getJobScheduler().schedule(jobInfoBuilder.build())
			if (result == JobScheduler.RESULT_FAILURE) {
				SDKLogger.e("Job scheduling has failed")
			}
		}
	}
}