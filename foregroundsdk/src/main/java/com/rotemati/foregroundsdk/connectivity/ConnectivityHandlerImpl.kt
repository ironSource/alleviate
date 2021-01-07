package com.rotemati.foregroundsdk.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getConnectivityManager
import com.rotemati.foregroundsdk.foregroundtask.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.foregroundtask.internal.logger.LoggerWrapper

internal class ConnectivityHandlerImpl : ConnectivityManager.NetworkCallback(), ConnectivityHandler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

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