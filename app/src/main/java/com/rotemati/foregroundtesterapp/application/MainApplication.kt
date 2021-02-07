package com.rotemati.foregroundtesterapp.application

import android.app.Application
import android.util.Log
import com.rotemati.foregroundsdk.external.foregroundSdk
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundtesterapp.sdk.BuildConfig

class MainApplication : Application() {
	override fun onCreate() {

		super.onCreate()
		foregroundSdk {
			context = this@MainApplication
//			logger = object : ForegroundLogger {
//				override val logsEnabled = BuildConfig.DEBUG
//				override val tag = "SAMPLE_APP"
//
//				override fun v(msg: String) {
//					Log.v(tag, msg)
//				}
//
//				override fun d(msg: String) {
//					Log.d(tag, msg)
//				}
//
//				override fun i(msg: String) {
//					Log.i(tag, msg)
//				}
//
//				override fun w(msg: String) {
//					Log.w(tag, msg)
//				}
//
//				override fun e(msg: String) {
//					Log.e(tag, msg)
//				}
//
//				override fun wtf(msg: String) {
//					Log.wtf(tag, msg)
//				}
//			}
		}
	}
}