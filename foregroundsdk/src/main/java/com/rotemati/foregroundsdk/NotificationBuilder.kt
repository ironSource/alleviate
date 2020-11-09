package com.rotemati.foregroundsdk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.rotemati.foregroundsdk.extensions.getNotificationManager

class NotificationBuilder(private val context: Context) {
    var channel: String = "General updates"
    var title: String = "Process updates"
    var body: String = "Initializing the experience"
    var iconRes: Int = android.R.drawable.stat_notify_sync

    @RequiresApi(Build.VERSION_CODES.O)
     fun createChannel() {
        val notificationChannel =
            NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.setSound(null, null)
        context.getNotificationManager().createNotificationChannel(notificationChannel)
    }

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

    fun build(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return NotificationCompat.Builder(context, channel)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(iconRes)
            .build()
    }
}