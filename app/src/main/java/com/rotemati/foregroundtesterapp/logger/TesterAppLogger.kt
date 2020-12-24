package com.rotemati.foregroundtesterapp.logger

import android.util.Log

private const val TAG = "FOREGROUND_TESTER_APP"

class TesterAppLogger {

	companion object {
		fun v(msg: String) {
			Log.v(TAG, msg)
		}

		fun d(msg: String) {
			Log.d(TAG, msg)
		}

		fun i(msg: String) {
			Log.i(TAG, msg)
		}

		fun w(msg: String) {
			Log.w(TAG, msg)
		}

		fun e(msg: String) {
			Log.e(TAG, msg)
		}

		fun wtf(msg: String) {
			Log.wtf(TAG, msg)
		}
	}
}