package com.rotemati.foregroundsdk.foregroundtask.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import kotlin.concurrent.thread

internal class MyPackageReplacedReceiver : BroadcastReceiver() {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)
	private val foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()

	override fun onReceive(context: Context, intent: Intent?) {
		thread {
			val pendingTasksRepository = PendingTasksRepository()
			pendingTasksRepository.getAll().forEach {
				logger.d("Rescheduling $it")
				foregroundTasksSchedulerWrapper.reschedule(it)
			}
		}
	}
}