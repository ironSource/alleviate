package com.rotemati.foregroundsdk.external.retryepolicy

data class RetryData(val retryPolicy: RetryPolicy, val initialBackoff: Long)

enum class RetryPolicy {
	Linear, Exponential
}