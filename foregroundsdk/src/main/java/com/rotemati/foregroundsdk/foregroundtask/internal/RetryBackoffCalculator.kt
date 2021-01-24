package com.rotemati.foregroundsdk.foregroundtask.internal

import com.rotemati.foregroundsdk.foregroundtask.external.reschedulepolicy.RetryPolicy
import java.util.concurrent.TimeUnit

internal class RetryBackoffCalculator {
	fun calculate(retryPolicy: RetryPolicy, retryCount: Int): Long {
		return when (retryPolicy) {
			RetryPolicy.Linear -> {
				retryCount * TimeUnit.SECONDS.toMillis(1)
			}
			RetryPolicy.Exponential -> {
				retryCount * retryCount * TimeUnit.SECONDS.toMillis(1)
			}
		}
	}
}