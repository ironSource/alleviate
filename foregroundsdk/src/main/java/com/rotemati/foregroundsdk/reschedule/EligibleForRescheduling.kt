package com.rotemati.foregroundsdk.reschedule

import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.logger.SDKLogger

class EligibleForRescheduling {
	fun isEligible(taskInfo: ForegroundTaskInfo): Boolean {
		return (taskInfo.rescheduleOnFail && taskInfo.retryCount < taskInfo.maxRetries).also {
			SDKLogger.i("Task ${taskInfo.id} eligible: $it")
		}
	}
}