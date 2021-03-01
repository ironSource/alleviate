package com.rotemati.foregroundsdk.internal.notification

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

internal class DefaultNotificationCreator {
	fun create(context: Context): Notification {
		val channelId = "Process updates"
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannelsCreator(context).createChannel(channelId)
		}
		return NotificationCompat.Builder(context, channelId)
				.setContentTitle("Process updates")
				.setContentText("Initializing the experience")
				.setSmallIcon(android.R.drawable.stat_notify_sync)
				.build()
	}
}