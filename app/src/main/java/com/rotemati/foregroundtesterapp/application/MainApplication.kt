package com.rotemati.foregroundtesterapp.application

import android.app.Application
import android.util.Log
import com.rotemati.foregroundsdk.external.ForegroundSDK
import com.rotemati.foregroundsdk.external.bucketpolling.BucketPollingData
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundtesterapp.BuildConfig
import java.util.concurrent.TimeUnit

class MainApplication : Application() {
	override fun onCreate() {

		super.onCreate()
		ForegroundSDK.context = this
		ForegroundSDK.bucketPollingData = BucketPollingData(
				interval = 5000,
				timeout = TimeUnit.MINUTES.toMillis(1)
		)
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