package com.rotemati.foregroundsdk.foregroundtask.external.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.reschedulepolicy.RetryPolicy
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.foregroundtask.internal.TriggerTimeCalculator
import com.rotemati.foregroundsdk.foregroundtask.internal.connectivity.*
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.notification.DefaultNotificationCreator
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.TaskInfoSpec
import java.util.*
import kotlin.concurrent.thread

private const val TASK_ID_NOT_VALID = -1
private const val DEFAULT_NOTIFICATION_ID = 654321

abstract class BaseForegroundTaskService : Service() {

	private lateinit var foregroundTasksSchedulerWrapper: ForegroundTasksSchedulerWrapper
	private lateinit var connectivityHandler: ConnectivityHandler
	private lateinit var pendingTasksRepository: PendingTasksRepository
	private lateinit var defaultNotificationCreator: DefaultNotificationCreator
	private lateinit var triggerTimeCalculator: TriggerTimeCalculator
	private lateinit var getConnectivityState: GetConnectivityState
	private lateinit var getConnectivityAllowance: GetConnectivityAllowance
	lateinit var foregroundTaskInfo: ForegroundTaskInfo

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	abstract fun startWork(): Result

	abstract fun getNotification(): Notification

	override fun onCreate() {
		super.onCreate()
		foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
		connectivityHandler = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			ConnectivityHandlerImplPost24()
		} else {
			ConnectivityHandlerImplPre24()
		}
		connectivityHandler.register(this)
		getConnectivityState = GetConnectivityState(this, connectivityHandler)
		getConnectivityAllowance = GetConnectivityAllowance()
		pendingTasksRepository = PendingTasksRepository()
		defaultNotificationCreator = DefaultNotificationCreator()
		triggerTimeCalculator = TriggerTimeCalculator()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		if (intent == null) {
			onError("intent is null")
			return START_NOT_STICKY
		}
		val jobId = intent.getIntExtra(EXTRA_TASK_ID, TASK_ID_NOT_VALID)

		if (jobId == TASK_ID_NOT_VALID) {
			onError("job id not valid")
			return START_NOT_STICKY
		}
		thread {
			val taskInfoSpec = pendingTasksRepository.getById(jobId)
			if (taskInfoSpec == null) {
				onError("job isn't in the repo")
				return@thread
			} else {
				foregroundTaskInfo = taskInfoSpec.foregroundTaskInfo
				val connectivityAllowance = getConnectivityAllowance(foregroundTaskInfo.networkType, getConnectivityState())
				if (connectivityAllowance is ConnectivityAllowance.NotAllowed) {
					onError(connectivityAllowance.reason, foregroundTaskInfo.id)
					logger.i("Scheduling connectivity job service")
					ConnectivityJobService.schedule(this, foregroundTaskInfo)
					return@thread
				}
				startForeground(foregroundTaskInfo.id, getNotification())
				when (val result = startWork()) {
					is Result.Success -> onSuccess()
					is Result.Failed -> onFailed()
					is Result.Reschedule -> onReschedule(taskInfoSpec, result.retryPolicy)
				}
			}
		}

		return START_NOT_STICKY
	}

	private fun onReschedule(taskInfoSpec: TaskInfoSpec, retryPolicy: RetryPolicy) {
		logger.i("Reschedule Task")
		val newRetryCount = taskInfoSpec.foregroundTaskInfo.retryCount.inc()
		foregroundTaskInfo = ForegroundTaskInfo(
				id = foregroundTaskInfo.id,
				networkType = foregroundTaskInfo.networkType,
				persisted = foregroundTaskInfo.persisted,
				minLatencyMillis = triggerTimeCalculator.calculate(retryPolicy, newRetryCount),
				timeoutMillis = foregroundTaskInfo.timeoutMillis,
				retryCount = newRetryCount,
				runImmediately = false
		)
		val newTaskInfoSpec = TaskInfoSpec(foregroundTaskInfo, taskInfoSpec.componentName)
		foregroundTasksSchedulerWrapper.reschedule(newTaskInfoSpec)
		finish()
	}

	private fun onFailed() {
		logger.i("Task failed - removing it from repo")
		pendingTasksRepository.remove(foregroundTaskInfo)
		finish()
	}

	private fun onSuccess() {
		logger.i("Task completed successfully - removing it from repo")
		pendingTasksRepository.remove(foregroundTaskInfo)
		finish()
	}

	private fun onError(error: String, notificationId: Int = DEFAULT_NOTIFICATION_ID) {
		logger.e(error)
		startForeground(notificationId, defaultNotificationCreator.create(this))
		finish()
	}

	private fun finish() {
		stopForeground(true)
		connectivityHandler.unregister(this)
		stopSelf()
	}

	override fun onBind(intent: Intent?): Nothing? = null

	companion object {
		const val EXTRA_TASK_ID = "com.ironsource.foreground.EXTRA_TASK_ID"
	}
}