package com.rotemati.foregroundsdk.internal.bucketpolling

internal interface BucketPollingStrategy {
    fun getCurrentBucket(): Int
}