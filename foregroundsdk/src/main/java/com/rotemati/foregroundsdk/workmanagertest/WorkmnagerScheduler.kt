package com.rotemati.foregroundsdk.workmanagertest

import android.content.Context
import androidx.work.*
import com.rotemati.foregroundsdk.foregroundtask.BaseForegroundTaskService.Companion.EXTRA_TASK_ID
import com.rotemati.foregroundsdk.foregroundtask.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.logger.SDKLogger

class WorkmnagerScheduler {

	fun schedule(
			context: Context,
			foregroundTaskInfo: ForegroundTaskInfo
	) {
		val pendingTasksRepository = PendingTasksRepository(context)
		SDKLogger.i("Saving taskId: ${foregroundTaskInfo.id}")
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