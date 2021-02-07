package com.rotemati.foregroundtesterapp.logger

import android.util.Log

private const val TAG = "FOREGROUND_TESTER_APP"

class TesterAppLogger {

	companion object {
		fun v(msg: String) {
			Log.v(TAG, "${threadName()} | $msg")
		}

		fun d(msg: String) {
			Log.d(TAG, "${threadName()} | $msg")
		}

		fun i(msg: String) {
			Log.i(TAG, "${threadName()} | $msg")
		}

		fun w(msg: String) {
			Log.w(TAG, "${threadName()} | $msg")
		}

		fun e(msg: String) {
			Log.e(TAG, "${threadName()} | $msg")
		}

		fun wtf(msg: String) {
			Log.wtf(TAG, "${threadName()} | $msg")
		}

		private fun threadName(): String = Thread.currentThread().name
	}
}