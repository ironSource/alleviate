package com.rotemati.foregroundsdk.foregroundtask.external.scheduler

import android.app.AlarmManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK.context
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.bucketpolling.BucketPoller
import com.rotemati.foregroundsdk.foregroundtask.internal.bucketpolling.BucketPollingStrategyImpl
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getAlarmManager
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.toDateFormat
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.notification.InternalForegroundServiceDisplayer
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksOperationsProvider
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksOperationsProviderPost26
import com.rotemati.foregroundsdk.foregroundtask.internal.scheduler.ForegroundTasksOperationsProviderPre26
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ForegroundTasksSchedulerWrapper {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val pendingTasksRepository = PendingTasksRepository()
	private val foregroundTasksOperationsProvider: ForegroundTasksOperationsProvider =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				ForegroundTasksOperationsProviderPost26(context)
			} else {
				ForegroundTasksOperationsProviderPre26(context)
			}
	private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
	private val internalForegroundServiceDisplayer = InternalForegroundServiceDisplayer(context)

	fun scheduleForegroundTask(className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			BucketPoller.strategy = BucketPollingStrategyImpl(context)
			if (BucketPoller.inNeverBucket) {
				logger.d("App is in 'Never' bucket - starting foreground service")
				internalForegroundServiceDisplayer.show()
				startBucketPolling()
			}
		}
		executorService.submit {
			val newForegroundTaskInfo = ForegroundTaskInfo(
					id = foregroundTaskInfo.id,
					networkType = foregroundTaskInfo.networkType,
					persisted = foregroundTaskInfo.persisted,
					minLatencyMillis = foregroundTaskInfo.minLatencyMillis,
					timeoutMillis = foregroundTaskInfo.timeoutMillis,
					retryCount = foregroundTaskInfo.retryCount,
					triggerTime = calculateTriggerTime(foregroundTaskInfo)
			)
			logger.i("Scheduling task to run at ${newForegroundTaskInfo.triggerTime.toDateFormat()}")
			val taskInfoSpec = TaskInfoSpec(newForegroundTaskInfo, className.name)
			pendingTasksRepository.save(taskInfoSpec)
			context.getAlarmManager().set(
					AlarmManager.RTC_WAKEUP,
					newForegroundTaskInfo.triggerTime,
					foregroundTasksOperationsProvider.createScheduleOperation(taskInfoSpec)
			)
		}
	}

	private fun calculateTriggerTime(foregroundTaskInfo: ForegroundTaskInfo): Long {
		return if (foregroundTaskInfo.isScheduled()) {
			foregroundTaskInfo.triggerTime
		} else {
			System.currentTimeMillis() + foregroundTaskInfo.minLatencyMillis
		}
	}

	@RequiresApi(Build.VERSION_CODES.P)
	private fun startBucketPolling() {
		BucketPoller.onNoLongerNeeded = { reason ->
			logger.d(reason)
			internalForegroundServiceDisplayer.dismiss()
		}
		BucketPoller.start()
	}

	fun cancel(taskId: Int) {
		executorService.submit {
			pendingTasksRepository.getTaskInfo(taskId)?.let { nonNullTaskSpec ->
				logger.i("cancel task $taskId")
				context.getAlarmManager().cancel(
						foregroundTasksOperationsProvider.createCancelOperation(nonNullTaskSpec)
				)
				pendingTasksRepository.remove(nonNullTaskSpec.foregroundTaskInfo)
			}
		}
	}

	companion object {
		const val ACTION_SHOW_NOTIFICATION = "com.rotemati.foregroundsdk.ACTION_SHOW_NOTIFICATION"
		const val EXTRA_NOTIFICATION = "com.rotemati.foregroundsdk.EXTRA_NOTIFICATION"
	}
}
