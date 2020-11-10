package com.rotemati.foregroundsdk.jobinfo

import com.rotemati.foregroundsdk.notification.DefaultNotificationDescriptorCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor

class ForegroundJobInfo(
		val id: Int,
		val networkType: Int,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val notificationDescriptor: NotificationDescriptor = DefaultNotificationDescriptorCreator().create(),
		val timeout: Long,
		val rescheduleOnFail: Boolean,
		val maxRetries: Int = 5,
		val retryCount: Int = 0
//    val foregroundObtainer: ForegroundObtainer
)

fun ForegroundJobInfo.latencyEpoch() = System.currentTimeMillis() + minLatencyMillis