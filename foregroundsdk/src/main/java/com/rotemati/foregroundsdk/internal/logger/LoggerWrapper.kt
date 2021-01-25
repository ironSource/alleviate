package com.rotemati.foregroundsdk.internal.logger

import com.rotemati.foregroundsdk.external.ForegroundSDK.foregroundLogger
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger

internal class LoggerWrapper(foregroundLogger: ForegroundLogger) : ForegroundLogger by foregroundLogger {

	override fun v(msg: String) {
		if (logsEnabled) {
			foregroundLogger.v(msg)
		}
	}

	override fun d(msg: String) {
		if (logsEnabled) {
			foregroundLogger.d(msg)
		}
	}

	override fun i(msg: String) {
		if (logsEnabled) {
			foregroundLogger.i(msg)
		}
	}

	override fun w(msg: String) {
		if (logsEnabled) {
			foregroundLogger.w(msg)
		}
	}

	override fun e(msg: String) {
		if (logsEnabled) {
			foregroundLogger.e(msg)
		}
	}

	override fun wtf(msg: String) {
		if (logsEnabled) {
			foregroundLogger.wtf(msg)
		}
	}

}

