package com.rotemati.foregroundsdk.jobinfo

import com.rotemati.foregroundsdk.foreground.ForegroundObtainer
import com.rotemati.foregroundsdk.notification.NotificationDescriptor

interface ForegroundJobInfoDSL {
	val id: Int
	val networkType: Int
	val persisted: Boolean
	val minLatencyMillis: Long
	val notificationDescriptor: NotificationDescriptor
	val timeout: Long
	val rescheduleOnFail: Boolean
	val maxRetries: Int
	val retryCount: Int
	val foregroundObtainer: ForegroundObtainer
}

class ForegroundJobInfoDSLImpl : ForegroundJobInfoDSL {
	override var id: Int = 0
	override var networkType: Int = 0
	override var persisted: Boolean = false
	override var minLatencyMillis: Long = 0
	override lateinit var notificationDescriptor: NotificationDescriptor
	override var timeout: Long = 0
	override var rescheduleOnFail: Boolean = false
	override var maxRetries: Int = 0
	override var retryCount: Int = 0
	override lateinit var foregroundObtainer: ForegroundObtainer

	fun build() = ForegroundJobInfo(id, networkType, persisted, minLatencyMillis, notificationDescriptor, timeout, rescheduleOnFail, maxRetries, retryCount, foregroundObtainer)
}

fun foregroundJobInfo(block: ForegroundJobInfoDSLImpl.() -> Unit): ForegroundJobInfo {
	return ForegroundJobInfoDSLImpl().apply(block).build()
}

