package com.rotemati.foregroundsdk.workmanagertest

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.rotemati.foregroundsdk.foregroundtask.BaseForegroundTaskService.Companion.EXTRA_TASK_ID
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.logger.SDKLogger
import com.rotemati.foregroundsdk.notification.NotificationChannelsCreator
import kotlinx.coroutines.delay

class MyWorker(
		context: Context, parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

	override suspend fun doWork(): Result {
		SDKLogger.logMethod()
		val jobId = inputData.getInt(EXTRA_TASK_ID, -1)
		if (jobId == -1) {
			SDKLogger.e("job id not valid")
			return Result.failure()
		}
		val jobInfo = PendingTasksRepository(applicationContext).pendingForegroundTasks.find { it.id == jobId }

		if (jobInfo == null) {
			SDKLogger.e("job isn't in the repo")
			return Result.failure()
		}
		setForeground(createForegroundInfo(jobInfo))
		delay(10000)
		return Result.success()
	}

	private fun createForegroundInfo(jobInfo: ForegroundTaskInfo): ForegroundInfo {
		NotificationChannelsCreator(applicationContext).createChannel(jobInfo.notification.channelId)
		return ForegroundInfo(jobInfo.id, jobInfo.notification)
	}
}