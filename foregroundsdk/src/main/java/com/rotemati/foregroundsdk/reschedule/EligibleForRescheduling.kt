package com.rotemati.foregroundsdk.reschedule

import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry.RetryPolicy
import com.rotemati.foregroundsdk.logger.SDKLogger

class EligibleForRescheduling {
	fun isEligible(taskInfo: ForegroundTaskInfo): Boolean {
		return when (taskInfo.retryPolicy) {
			is RetryPolicy.Retry -> {
				(taskInfo.retryPolicy.rescheduleOnFail && taskInfo.retryCount < taskInfo.retryPolicy.maxRetries).also {
					SDKLogger.i("Task ${taskInfo.id} eligible: $it")
				}
			}
			is RetryPolicy.NoRetry -> false
		}
	}
}