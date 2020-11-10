package com.rotemati.foregroundsdk

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundsdk.notification.NotificationChannelsCreator
import com.rotemati.foregroundsdk.notification.NotificationDescriptor

class NotificationBuilder(private val context: Context,
                          private val notificationChannelsCreator: NotificationChannelsCreator
) {
	var channel: String = "General updates"
	var title: String = "Process updates"
	var body: String = "Initializing the experience"
	var iconRes: Int = android.R.drawable.stat_notify_sync

	fun channel(init: () -> String) {
		channel = init()
	}

	fun title(init: () -> String): NotificationBuilder {
		title = init()
		return this
	}

	fun body(init: () -> String) {
		body = init()
	}

	fun iconRes(init: () -> Int) {
		iconRes = init()
	}

	fun build(notificationDescriptor: NotificationDescriptor): Notification {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationChannelsCreator.createChannel(channel)
		}
		return NotificationCompat.Builder(context, channel)
				.setContentTitle(notificationDescriptor.title)
				.setContentText(notificationDescriptor.body)
				.setSmallIcon(notificationDescriptor.iconResId)
				.build()
	}
}