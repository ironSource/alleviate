package com.rotemati.foregroundsdk.foregroundtask.external

import com.rotemati.foregroundsdk.foregroundtask.internal.logger.DefaultSDKLogger
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger

object ForegroundSDK {
	var foregroundLogger: ForegroundLogger = DefaultSDKLogger
}