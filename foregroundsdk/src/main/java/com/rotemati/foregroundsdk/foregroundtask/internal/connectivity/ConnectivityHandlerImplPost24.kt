package com.rotemati.foregroundsdk.foregroundtask.internal.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getConnectivityManager

@RequiresApi(Build.VERSION_CODES.N)
internal class ConnectivityHandlerImplPost24 : ConnectivityManager.NetworkCallback(), ConnectivityHandler {

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
		context.getConnectivityManager().registerDefaultNetworkCallback(this)
	}

	override fun unregister(context: Context) {
		context.getConnectivityManager().unregisterNetworkCallback(this)
	}
}