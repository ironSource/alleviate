package com.rotemati.foregroundsdk.foregroundtask.taskinfo

import com.rotemati.foregroundsdk.foregroundtask.ForegroundObtainer
import com.rotemati.foregroundsdk.notification.DefaultNotificationDescriptorCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor

class ForegroundTaskInfo(
		val id: Int,
		val networkType: Int,
		val persisted: Boolean,
		val minLatencyMillis: Long,
		val notificationDescriptor: NotificationDescriptor = DefaultNotificationDescriptorCreator().create(),
		val timeout: Long,
		val rescheduleOnFail: Boolean,
		val maxRetries: Int = 5,
		val retryCount: Int = 0,
		val foregroundObtainer: ForegroundObtainer
)

fun ForegroundTaskInfo.latencyEpoch() = System.currentTimeMillis() + minLatencyMillis