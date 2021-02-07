package com.rotemati.foregroundsdk.external

import android.content.Context
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.internal.logger.DefaultSDKLogger

object ForegroundSdk {
    var logger: ForegroundLogger = DefaultSDKLogger
    lateinit var context: Context
}