package com.rotemati.foregroundsdk.foregroundtask.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository

internal class MyPackageReplacedReceiver : BroadcastReceiver() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	override fun onReceive(context: Context, intent: Intent?) {
		val pendingTasksRepository = PendingTasksRepository(context)
		val foregroundTasksScheduler = ForegroundTasksSchedulerWrapper(context)
		pendingTasksRepository.getAll { tasks ->
			tasks.forEach {
				logger.d("Rescheduling $it")
				foregroundTasksScheduler.reschedule(it)
			}
		}
	}
}