package com.rotemati.foregroundsdk.foregroundtask.internal

import com.rotemati.foregroundsdk.foregroundtask.external.reschedulepolicy.RetryPolicy

internal class TriggerTimeCalculator {
	fun calculate(retryPolicy: RetryPolicy, retryCount: Int): Long {
		val time = when (retryPolicy) {
			RetryPolicy.Linear -> {
				retryCount * 1000
			}
			RetryPolicy.Exponential -> {
				retryCount * 1000 * retryCount
			}
		}
		return time.toLong()
	}
}