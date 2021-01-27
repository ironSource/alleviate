package com.rotemati.foregroundsdk.internal.logger

import com.rotemati.foregroundsdk.external.ForegroundSDK.foregroundLogger
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger

internal class LoggerWrapper(foregroundLogger: ForegroundLogger) : ForegroundLogger by foregroundLogger {

	override fun v(msg: String) {
		if (logsEnabled) {
			foregroundLogger.v("${threadName()} | $msg")
		}
	}

	override fun d(msg: String) {
		if (logsEnabled) {
			foregroundLogger.d("${threadName()} | $msg")
		}
	}

	override fun i(msg: String) {
		if (logsEnabled) {
			foregroundLogger.i("${threadName()} | $msg")
		}
	}

	override fun w(msg: String) {
		if (logsEnabled) {
			foregroundLogger.w("${threadName()} | $msg")
		}
	}

	override fun e(msg: String) {
		if (logsEnabled) {
			foregroundLogger.e("${threadName()} | $msg")
		}
	}

	override fun wtf(msg: String) {
		if (logsEnabled) {
			foregroundLogger.wtf("${threadName()} | $msg")
		}
	}

	private fun threadName(): String = Thread.currentThread().name
}

