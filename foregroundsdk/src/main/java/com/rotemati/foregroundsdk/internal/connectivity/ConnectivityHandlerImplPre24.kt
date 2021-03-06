package com.rotemati.foregroundsdk.internal.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.rotemati.foregroundsdk.external.ForegroundSdk
import com.rotemati.foregroundsdk.internal.extensions.getConnectivityManager

private const val ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"

internal class ConnectivityHandlerImplPre24 : ConnectivityHandler, BroadcastReceiver() {

	private var onConnectivityChanged: ConnectivityChangedListener? = null

	override val connected
		get() = isConnected(ForegroundSdk.context)

	override val roaming: Boolean
		get() = isRoamingOld(ForegroundSdk.context)

	override val blocked = false

	override fun setConnectivityListener(listener: ConnectivityChangedListener) {
		this.onConnectivityChanged = listener
	}

	override fun onReceive(context: Context?, intent: Intent?) {
		onConnectivityChanged?.onChanged()
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