package com.rotemati.foregroundsdk.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.rotemati.foregroundsdk.extensions.getConnectivityManager
import com.rotemati.foregroundsdk.logger.SDKLogger

internal class ConnectivityHandlerImpl : ConnectivityManager.NetworkCallback(), ConnectivityHandler {

	override fun onAvailable(network: Network) {
		super.onAvailable(network)
		hasInternetAccess = true
	}

	override fun onUnavailable() {
		super.onUnavailable()
		hasInternetAccess = false
	}

	override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
		super.onBlockedStatusChanged(network, blocked)
		isBlocked = blocked
	}

	override var hasInternetAccess = false

	override var isBlocked = false

	override fun register(context: Context) {
		SDKLogger.logMethod()
		context.getConnectivityManager().registerDefaultNetworkCallback(this)
	}

	override fun unregister(context: Context) {
		SDKLogger.logMethod()
		context.getConnectivityManager().unregisterNetworkCallback(this)
	}
}