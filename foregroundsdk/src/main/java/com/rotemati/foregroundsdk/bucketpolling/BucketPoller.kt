package com.rotemati.foregroundsdk.bucketpolling

import kotlinx.coroutines.flow.Flow

interface BucketPoller {
	fun poll(): Flow<Int>
	fun stop()
}