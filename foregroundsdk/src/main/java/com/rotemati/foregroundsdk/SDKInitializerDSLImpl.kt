package com.rotemati.foregroundsdk

import android.app.Notification
import android.content.Context
import com.ironsource.aura.dslint.annotations.DSLMandatory
import com.ironsource.aura.dslint.annotations.DSLint
import java.util.concurrent.TimeUnit

@DSLint
interface SDKInitializerDSL {

	@set:DSLMandatory
	var context: Context
	var notification: Notification
	var bucketPollingDelay: Long
	var bucketPollingTimeout: Long
}

class SDKInitializerDSLImpl : SDKInitializerDSL {
	override lateinit var context: Context
	override lateinit var notification: Notification
	override var bucketPollingDelay: Long = TimeUnit.SECONDS.toMillis(5)
	override var bucketPollingTimeout: Long = TimeUnit.MINUTES.toMillis(5)

	fun context(block: () -> Context) {
		context = block()
	}

	fun bucketPollingDelay(block: () -> Long) {
		bucketPollingDelay = block()
	}

	fun bucketPollingTimeout(block: () -> Long) {
		bucketPollingTimeout = block()
	}

	fun notification(block: NotificationBuilder.() -> Unit) {
		val notificationBuilder = NotificationBuilder(context)
		notificationBuilder.block()
		notification = notificationBuilder.build()
	}

	fun build() = SDKInitializer(context, bucketPollingTimeout, notification, bucketPollingDelay)
}

fun sdkInitializer(block: SDKInitializerDSLImpl.() -> Unit) =
		SDKInitializerDSLImpl().apply(block).build()

//fun test() {
//    sdkInitializer {
//
//
//    }
//}