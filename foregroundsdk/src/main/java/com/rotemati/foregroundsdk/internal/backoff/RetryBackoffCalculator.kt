package com.rotemati.foregroundsdk.internal.backoff

import com.rotemati.foregroundsdk.external.retryepolicy.RetryData
import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy
import kotlin.math.pow


internal class RetryBackoffCalculator {
	fun calculate(retryData: RetryData, retryCount: Int): Long {
		return when (retryData.retryPolicy) {
			RetryPolicy.Linear -> {
				retryCount * retryData.initialBackoff
			}
			RetryPolicy.Exponential -> {
				(retryData.initialBackoff * 2).pow(retryCount - 1)
			}
		}
	}
}

private inline fun Long.pow(x: Int): Long {
	return this.toDouble().pow(x).toLong()
}

