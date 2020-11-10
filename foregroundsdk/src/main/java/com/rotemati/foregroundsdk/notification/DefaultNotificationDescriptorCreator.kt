package com.rotemati.foregroundsdk.notification

class DefaultNotificationDescriptorCreator {
	fun create() = notificationDescriptor {
		title = "Process updates"
		body = "Initializing the experience"
		iconResId = android.R.drawable.stat_notify_sync
	}
}