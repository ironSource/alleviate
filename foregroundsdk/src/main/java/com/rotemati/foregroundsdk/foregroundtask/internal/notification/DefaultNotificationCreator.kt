package com.rotemati.foregroundsdk.foregroundtask.internal.notification

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundtesterapp.sdk.R

internal class DefaultNotificationCreator {
	fun create(context: Context): Notification {
		val resources = context.resources
		val channelId = resources.getString(R.string.default_foreground_notification_channel_id)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannelsCreator(context).createChannel(channelId)
		}
		return NotificationCompat.Builder(context, channelId)
				.setContentTitle(resources.getString(R.string.default_foreground_notification_title))
				.setContentText(resources.getString(R.string.default_foreground_notification_body))
				.setSmallIcon(android.R.drawable.stat_notify_sync)
				.build()
	}
}