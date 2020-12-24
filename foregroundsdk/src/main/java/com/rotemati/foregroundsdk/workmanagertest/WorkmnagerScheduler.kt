package com.rotemati.foregroundsdk.workmanagertest

import android.content.Context
import androidx.work.*
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.services.BaseForegroundTaskService.Companion.EXTRA_TASK_ID
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper

class WorkmnagerScheduler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	fun schedule(
			context: Context,
			foregroundTaskInfo: ForegroundTaskInfo
	) {
		val pendingTasksRepository = PendingTasksRepository(context)
		logger.i("Saving taskId: ${foregroundTaskInfo.id}")
		pendingTasksRepository.save(foregroundTaskInfo, "")

		val constraints = Constraints.Builder()
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build()

		val data = EXTRA_TASK_ID to foregroundTaskInfo.id

		val uploadWorkRequest = OneTimeWorkRequestBuilder<MyWorker>()
				.setInputData(
						workDataOf(
								data
						)
				)
				.setConstraints(constraints)
				.build()

		WorkManager
				.getInstance(context)
				.enqueue(uploadWorkRequest)
	}
}