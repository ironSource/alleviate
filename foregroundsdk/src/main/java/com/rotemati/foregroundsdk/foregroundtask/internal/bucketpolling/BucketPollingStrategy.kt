package com.rotemati.foregroundsdk.foregroundtask.internal.bucketpolling

interface BucketPollingStrategy {
	fun getCurrentBucket(): Int
}