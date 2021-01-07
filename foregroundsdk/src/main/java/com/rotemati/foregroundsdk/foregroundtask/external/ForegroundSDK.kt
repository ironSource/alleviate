package com.rotemati.foregroundsdk.foregroundtask.external

import android.content.Context
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.DefaultSDKLogger

object ForegroundSDK {
	var foregroundLogger: ForegroundLogger = DefaultSDKLogger
	lateinit var context: Context
}