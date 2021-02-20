package com.rotemati.foregroundsdk.external

import android.content.Context
import com.ironsource.aura.dslint.annotations.DSLMandatory
import com.ironsource.aura.dslint.annotations.DSLint
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.internal.logger.DefaultSDKLogger

@DSLint
class ForegroundSdkDSL {
	@set:DSLMandatory(message = "Context must be set")
	lateinit var context: Context
	var logger: ForegroundLogger = DefaultSDKLogger

	fun build(): ForegroundSdk {
		ForegroundSdk.context = context
		ForegroundSdk.logger = logger
		return ForegroundSdk
	}
}

fun foregroundSdk(block: ForegroundSdkDSL.() -> Unit): ForegroundSdk {
	return ForegroundSdkDSL().apply(block).build()
}