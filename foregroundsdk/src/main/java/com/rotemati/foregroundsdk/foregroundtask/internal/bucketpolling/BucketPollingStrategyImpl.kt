package com.rotemati.foregroundsdk.foregroundtask.internal.bucketpolling

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getStandbyBucket

class BucketPollingStrategyImpl(private val context: Context) : BucketPollingStrategy {
	@RequiresApi(Build.VERSION_CODES.P)
	override fun getCurrentBucket(): Int {
		return context.getStandbyBucket()
	}
}