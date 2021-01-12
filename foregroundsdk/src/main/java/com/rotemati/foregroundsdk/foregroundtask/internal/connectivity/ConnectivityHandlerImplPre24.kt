package com.rotemati.foregroundsdk.foregroundtask.internal.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getConnectivityManager

private const val ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"

internal class ConnectivityHandlerImplPre24 : ConnectivityHandler, BroadcastReceiver() {

	override var hasInternetAccess = false

	override var isBlocked = false

	override fun onReceive(context: Context?, intent: Intent?) {
		context?.let { nonNullContext ->
			hasInternetAccess = isConnected(nonNullContext)
		}
	}

	override fun register(context: Context) {
		context.registerReceiver(this, IntentFilter(ACTION_CONNECTIVITY_CHANGE))
	}

	override fun unregister(context: Context) {
		context.unregisterReceiver(this)
	}

	private fun isConnected(context: Context): Boolean {
		val networkInfo = context.getConnectivityManager().activeNetworkInfo
		return networkInfo != null && networkInfo.isConnected
	}
}