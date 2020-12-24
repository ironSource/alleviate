package com.rotemati.foregroundsdk.workmanagertest

import android.app.Notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.services.BaseForegroundTaskService.Companion.EXTRA_TASK_ID
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import kotlinx.coroutines.delay

class MyWorker(
		context: Context, parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	override suspend fun doWork(): Result {
		val jobId = inputData.getInt(EXTRA_TASK_ID, -1)
		if (jobId == -1) {
			logger.e("job id not valid")
			return Result.failure()
		}
		val jobInfo = PendingTasksRepository(applicationContext).foregroundTasks.find { it.id == jobId }

		if (jobInfo == null) {
			logger.e("job isn't in the repo")
			return Result.failure()
		}
		setForeground(createForegroundInfo(jobInfo))
		delay(10000)
		return Result.success()
	}

	private fun createForegroundInfo(jobInfo: ForegroundTaskInfo): ForegroundInfo {
		return ForegroundInfo(jobInfo.id, createNotification())
	}

	private fun createNotification(): Notification {
		TODO("Not yet implemented")
	}
}