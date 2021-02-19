package com.rotemati.foregroundsdk.external.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.external.ForegroundSdk
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.external.stopinfo.StoppedCause
import com.rotemati.foregroundsdk.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.internal.backoff.RetryBackoffCalculator
import com.rotemati.foregroundsdk.internal.connectivity.*
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.internal.notification.DefaultNotificationCreator
import com.rotemati.foregroundsdk.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec

private const val TASK_ID_NOT_VALID = -1
private const val DEFAULT_NOTIFICATION_ID = 654321

abstract class BaseForegroundTaskService : Service() {

	private lateinit var foregroundTasksSchedulerWrapper: ForegroundTasksSchedulerWrapper
	private lateinit var connectivityHandler: ConnectivityHandler
	private lateinit var pendingTasksRepository: PendingTasksRepository
	private lateinit var defaultNotificationCreator: DefaultNotificationCreator
	private lateinit var retryBackoffCalculator: RetryBackoffCalculator
	private lateinit var getConnectivityState: GetConnectivityState
	private lateinit var getConnectivityAllowance: GetConnectivityAllowance
	lateinit var taskInfoSpec: TaskInfoSpec
	private var finishedGracefully: Boolean = false

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSdk.logger)

	abstract fun startWork(): Result

	abstract fun getNotification(): Notification

	abstract fun doStop(stoppedCause: StoppedCause): Result

	override fun onCreate() {
		super.onCreate()
		foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
		connectivityHandler = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			ConnectivityHandlerImplPost24()
		} else {
			ConnectivityHandlerImplPre24()
		}
		connectivityHandler.register(this)
		getConnectivityState = GetConnectivityState(connectivityHandler)
		getConnectivityAllowance = GetConnectivityAllowance()
		pendingTasksRepository = PendingTasksRepository(this)
		defaultNotificationCreator = DefaultNotificationCreator()
		retryBackoffCalculator = RetryBackoffCalculator()
	}

	override fun onDestroy() {
		super.onDestroy()
		connectivityHandler.unregister(this)
		logger.d("finishedGracefully onDestroy: $finishedGracefully")
		if (!finishedGracefully) {
			when (doStop(StoppedCause.TerminatedBySystem)) {
				Result.Failed -> onFailed()
				Result.Success -> onSuccess()
				Result.Retry -> onReschedule()
			}
		}
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		logger.d("finishedGracefully onStartCommand: $finishedGracefully")
		if (intent == null) {
			onError("intent is null")
			return START_NOT_STICKY
		}
		val jobId = intent.getIntExtra(EXTRA_TASK_ID, TASK_ID_NOT_VALID)

		if (jobId == TASK_ID_NOT_VALID) {
			onError("job id not valid")
			return START_NOT_STICKY
		}
		val retrievedTaskInfoSpec = pendingTasksRepository.getTaskInfo(jobId)
		if (retrievedTaskInfoSpec == null) {
			onError("job isn't in the repo")
			return START_NOT_STICKY
		} else {
			taskInfoSpec = retrievedTaskInfoSpec
			val foregroundTaskInfo = taskInfoSpec.foregroundTaskInfo
			val connectivityAllowance = getConnectivityAllowance(foregroundTaskInfo.networkType, getConnectivityState)
			if (connectivityAllowance is ConnectivityAllowance.NotAllowed) {
				onError(connectivityAllowance.reason, foregroundTaskInfo.id)
				logger.i("Scheduling connectivity job service")
				ConnectivityJobService.schedule(this, foregroundTaskInfo)
				return START_NOT_STICKY
			}
			connectivityHandler.setConnectivityListener {
				logger.d("onConnectivityEvent")
				if (!finishedGracefully) {
					if (getConnectivityAllowance(foregroundTaskInfo.networkType, getConnectivityState) is ConnectivityAllowance.NotAllowed) {
						logger.d("ConnectionNotAllowed")
						when (doStop(StoppedCause.ConnectionNotAllowed)) {
							Result.Failed -> onFailed()
							Result.Success -> onSuccess()
							Result.Retry -> onReschedule()
						}
					}
				} else {
					logger.d("ignoring onConnectivityEvent - service finished gracefully")
				}
			}
			startForeground(foregroundTaskInfo.id, getNotification())
			when (startWork()) {
				Result.Success -> onSuccess()
				Result.Failed -> onFailed()
				Result.Retry -> onReschedule()
			}
		}
		return START_NOT_STICKY
	}

	private fun onReschedule() {
		logger.i("Reschedule Task")
		val newRetryCount = taskInfoSpec.foregroundTaskInfo.retryCount.inc()
		val foregroundTaskInfo = taskInfoSpec.foregroundTaskInfo.copy(
				minLatencyMillis = retryBackoffCalculator.calculate(taskInfoSpec.foregroundTaskInfo.retryData, newRetryCount),
				retryCount = newRetryCount,
		)
		foregroundTasksSchedulerWrapper.scheduleForegroundTask(
				Class.forName(taskInfoSpec.componentName),
				foregroundTaskInfo
		)
		finish()
	}

	private fun onFailed() {
		logger.i("Task failed - removing it from repo")
		pendingTasksRepository.delete(taskInfoSpec.foregroundTaskInfo.id)
		finish()
	}

	private fun onSuccess() {
		logger.i("Task completed successfully - removing it from repo")
		pendingTasksRepository.delete(taskInfoSpec.foregroundTaskInfo.id)
		finish()
	}

	private fun onError(error: String, notificationId: Int = DEFAULT_NOTIFICATION_ID) {
		logger.e(error)
		startForeground(notificationId, defaultNotificationCreator.create(this))
		finish()
	}

	private fun finish() {
		finishedGracefully = true
		stopForeground(true)
		connectivityHandler.unregister(this)
		logger.d("stopSelf")
		stopSelf()
	}

	override fun onBind(intent: Intent?): Nothing? = null

	companion object {
		const val EXTRA_TASK_ID = "com.ironsource.foreground.EXTRA_TASK_ID"
	}
}