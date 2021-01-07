package com.rotemati.foregroundsdk.foregroundtask.internal.scheduler

import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo

interface ForegroundTasksScheduler {
	fun schedule(className: Class<*>, foregroundTaskInfo: ForegroundTaskInfo)
	fun alreadyScheduled(foregroundTaskInfo: ForegroundTaskInfo): Boolean
	fun cancel(foregroundTaskInfo: ForegroundTaskInfo)
}