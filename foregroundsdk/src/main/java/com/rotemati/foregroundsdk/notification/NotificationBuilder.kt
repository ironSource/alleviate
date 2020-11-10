package com.rotemati.foregroundsdk.notification

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