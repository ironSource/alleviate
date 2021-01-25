package com.rotemati.foregroundsdk.internal.logger

import android.util.Log
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger

internal object DefaultSDKLogger : ForegroundLogger {
	override val logsEnabled = true
	override val tag = "FOREGROUND_SDK"

	override fun v(msg: String) {
		Log.v(tag, msg)
	}

	override fun d(msg: String) {
		Log.d(tag, msg)
	}

	override fun i(msg: String) {
		Log.i(tag, msg)
	}

	override fun w(msg: String) {
		Log.w(tag, msg)
	}

	override fun e(msg: String) {
		Log.e(tag, msg)
	}

	override fun wtf(msg: String) {
		Log.wtf(tag, msg)
	}
}