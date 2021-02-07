package com.rotemati.foregroundsdk.internal.logger

import com.rotemati.foregroundsdk.external.ForegroundSdk.logger
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger

internal class LoggerWrapper(foregroundLogger: ForegroundLogger) : ForegroundLogger by foregroundLogger {

	override fun v(msg: String) {
		if (logsEnabled) {
			logger.v("${threadName()} | $msg")
		}
	}

	override fun d(msg: String) {
		if (logsEnabled) {
			logger.d("${threadName()} | $msg")
		}
	}

	override fun i(msg: String) {
		if (logsEnabled) {
			logger.i("${threadName()} | $msg")
		}
	}

	override fun w(msg: String) {
		if (logsEnabled) {
			logger.w("${threadName()} | $msg")
		}
	}

	override fun e(msg: String) {
		if (logsEnabled) {
			logger.e("${threadName()} | $msg")
		}
	}

	override fun wtf(msg: String) {
		if (logsEnabled) {
			logger.wtf("${threadName()} | $msg")
		}
	}

	private fun threadName(): String = Thread.currentThread().name
}

