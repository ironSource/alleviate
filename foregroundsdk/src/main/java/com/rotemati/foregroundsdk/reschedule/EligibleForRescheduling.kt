package com.rotemati.foregroundsdk.reschedule

import com.rotemati.foregroundsdk.jobinfo.ForegroundJobInfo
import com.rotemati.foregroundsdk.logger.SDKLogger

class EligibleForRescheduling {
	fun isEligible(jobInfo: ForegroundJobInfo): Boolean {
		return (jobInfo.rescheduleOnFail && jobInfo.retryCount < jobInfo.maxRetries).also {
			SDKLogger.i("Job ${jobInfo.id} eligible: $it")
		}
	}
}