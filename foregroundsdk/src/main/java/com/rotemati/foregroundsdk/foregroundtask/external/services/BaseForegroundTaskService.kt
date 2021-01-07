package com.rotemati.foregroundsdk.foregroundtask.external.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandler
import com.rotemati.foregroundsdk.connectivity.ConnectivityHandlerImpl
import com.rotemati.foregroundsdk.connectivity.ConnectivityJobService
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.external.scheduler.ForegroundTasksSchedulerWrapper
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.result.Result
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper
import com.rotemati.foregroundsdk.foregroundtask.internal.notification.DefaultNotificationCreator
import com.rotemati.foregroundsdk.foregroundtask.internal.repositories.PendingTasksRepository

private const val JOB_ID_NOT_VALID: Int = -1
private const val NOTIFICATION_ID: Int = 654321

abstract class BaseForegroundTaskService : Service() {

	private lateinit var foregroundTasksSchedulerWrapper: ForegroundTasksSchedulerWrapper
	private lateinit var mConnectivityHandler: ConnectivityHandler
	private lateinit var pendingTasksRepository: PendingTasksRepository
	private lateinit var defaultNotificationCreator: DefaultNotificationCreator
	private lateinit var foregroundTaskInfo: ForegroundTaskInfo

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	abstract fun startWork(): Result // todo make this method work on background thread

	abstract fun getNotification(): Notification

	fun getForegroundTaskInfo() = foregroundTaskInfo //change to val

	override fun onCreate() {
		super.onCreate()
		foregroundTasksSchedulerWrapper = ForegroundTasksSchedulerWrapper()
		mConnectivityHandler = ConnectivityHandlerImpl()
		mConnectivityHandler.register(this)
		pendingTasksRepository = PendingTasksRepository()
		defaultNotificationCreator = DefaultNotificationCreator()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		super.onStartCommand(intent, flags, startId)
		if (intent == null) {
			return START_NOT_STICKY
		}
		val jobId = intent.getIntExtra(EXTRA_TASK_ID, JOB_ID_NOT_VALID)

		if (jobId == JOB_ID_NOT_VALID) {
			onError("job id not valid")
			return START_NOT_STICKY
		}
		var taskInfo: ForegroundTaskInfo?
		pendingTasksRepository.getById(jobId) { task ->
			taskInfo = task
			if (taskInfo == null) {
				onError("job isn't in the repo")
			} else {
				foregroundTaskInfo = taskInfo!!
			}
			if (!isConnectionAllowed(foregroundTaskInfo.networkType)) {
				onError("Connection type isn't allowed")
				logger.i("Scheduling connectivity job service")
				ConnectivityJobService.schedule(this, foregroundTaskInfo)
			}

			val foregroundNotification = getNotification()
			logger.d("Showing foreground")
			startForeground(NOTIFICATION_ID, foregroundNotification)

			when (val result = startWork()) {
				is Result.Success -> onFinished()
				is Result.Failed -> onFailed(result.throwable)
				is Result.Reschedule -> onReschedule()
			}
		}

		return START_NOT_STICKY
	}

	private fun onReschedule() {
		foregroundTaskInfo = ForegroundTaskInfo(
				id = foregroundTaskInfo.id,
				networkType = foregroundTaskInfo.networkType,
				persisted = foregroundTaskInfo.persisted,
				minLatencyMillis = foregroundTaskInfo.minLatencyMillis,
				timeoutMillis = foregroundTaskInfo.timeoutMillis,
				retryCount = foregroundTaskInfo.retryCount + 1
		)
		foregroundTasksSchedulerWrapper.reschedule(foregroundTaskInfo)
		logger.d("Stopping foreground")
		stopForeground(true)
		stopSelf()
	}

	private fun onFailed(throwable: Throwable?) {
		throwable?.message?.let { logger.e(it) }
		pendingTasksRepository.remove(foregroundTaskInfo)
		logger.d("Stopping foreground")
		stopForeground(true)
		stopSelf()
	}

	private fun onFinished() {
		logger.i("Task completed successfully - removing it from repo")
		pendingTasksRepository.remove(foregroundTaskInfo)
		logger.d("Stopping foreground")
		stopForeground(true)
		stopSelf()
	}

	private fun onError(error: String) {
		logger.e(error)
		startForeground(NOTIFICATION_ID, defaultNotificationCreator.create(this))
		stopForeground(true)
		stopSelf()
	}

	private fun isConnectionAllowed(networkType: NetworkType): Boolean {
		if (networkType == NetworkType.None) {
			logger.i("Task doesn't require any network")
			return true
		} else {
			if (networkType == NetworkType.NotRoaming && mConnectivityHandler.isRoaming(this)) {
				logger.i("Task requires not roaming but in roaming")
				return false
			} else {
				if (mConnectivityHandler.isBlocked) {
					logger.i("Task requires network but network is blocked")
					return false
				}
				if (!mConnectivityHandler.isConnected(this)) {
					logger.i("Task Requires network but no internet connection")
					return false
				}
			}
			return true
		}
	}

	override fun onBind(intent: Intent?): Nothing? = null

	companion object {
		const val EXTRA_TASK_ID = "com.ironsource.foreground.EXTRA_TASK_ID"
	}
}