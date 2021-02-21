package com.rotemati.foregroundsdk.external

import android.content.Context
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.internal.logger.DefaultSDKLogger

object ForegroundSdk {
	var logger: ForegroundLogger = DefaultSDKLogger
	lateinit var context: Context

	class Builder(private val context: Context) {

		private var logger: ForegroundLogger = DefaultSDKLogger

		fun foregroundLogger(logger: ForegroundLogger) = apply { this.logger = logger }

		fun build(): ForegroundSdk {
			return foregroundSdk {
				context = this@Builder.context
				logger = this@Builder.logger
			}
		}
	}
}