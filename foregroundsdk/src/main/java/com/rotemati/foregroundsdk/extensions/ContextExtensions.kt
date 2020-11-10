package com.rotemati.foregroundsdk.extensions

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
internal fun Context.getStandbyBucket(): Int {
	return getSystemService(UsageStatsManager::class.java).appStandbyBucket
}

fun Context.getNotificationManager(): NotificationManager {
	return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

internal fun Context.getConnectivityManager(): ConnectivityManager {
	return getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

internal fun Context.getAlarmManager(): AlarmManager {
	return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}