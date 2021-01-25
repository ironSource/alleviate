package com.rotemati.foregroundsdk.bucketPoller.mocks

import com.rotemati.foregroundsdk.internal.bucketpolling.BucketPollingStrategy

private const val STANDBY_BUCKET_NEVER = 50

class MockBucketPollingStrategy : BucketPollingStrategy {
	var mockValue = STANDBY_BUCKET_NEVER

	override fun getCurrentBucket(): Int {
		return mockValue
	}
}