package com.rotemati.foregroundsdk.foregroundtask.internal.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getNotificationManager

internal class NotificationChannelsCreator(private val context: Context) {
	@RequiresApi(Build.VERSION_CODES.O)
	fun createChannel(channel: String) {
		val notificationChannel =
				NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_DEFAULT)
		notificationChannel.setSound(null, null)
		context.getNotificationManager().createNotificationChannel(notificationChannel)
	}
}