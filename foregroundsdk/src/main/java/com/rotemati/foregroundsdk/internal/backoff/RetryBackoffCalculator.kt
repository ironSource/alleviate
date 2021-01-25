package com.rotemati.foregroundsdk.internal.backoff

import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy
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