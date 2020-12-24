package com.rotemati.foregroundtesterapp.application

import android.app.Application
import android.util.Log
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundtesterapp.BuildConfig

class MainApplication : Application() {
	override fun onCreate() {

		super.onCreate()

		ForegroundSDK.foregroundLogger = object : ForegroundLogger {
			override val logsEnabled = BuildConfig.DEBUG
			override val tag = "SAMPLE APP"

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
	}
}