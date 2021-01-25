package com.rotemati.foregroundsdk.internal.bucketpolling

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.internal.extensions.getStandbyBucket

internal class BucketPollingStrategyImpl(private val context: Context) : BucketPollingStrategy {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun getCurrentBucket(): Int {
        return context.getStandbyBucket()
    }
}