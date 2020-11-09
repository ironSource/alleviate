package com.rotemati.foregroundsdk.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.extensions.getConnectivityManager
import com.rotemati.foregroundsdk.logger.SDKLogger

class ConnectivityEventsHandlerImpl(
	private val context: Context
) : ConnectivityManager.NetworkCallback(), ConnectivityEventsHandler {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        hasInternetAccess = true
        SDKLogger.logMethod()
    }

    override fun onUnavailable() {
        super.onUnavailable()
        hasInternetAccess = false
        SDKLogger.logMethod()
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        super.onBlockedStatusChanged(network, blocked)
        SDKLogger.logMethod()
        isBlocked = blocked
        SDKLogger.d("blocked: $blocked")
    }

    override var hasInternetAccess = isConnected(context)

    override var isBlocked = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun register() {
        SDKLogger.logMethod()
        context.getConnectivityManager().registerDefaultNetworkCallback(this)
    }

    override fun unregister() {
        SDKLogger.logMethod()
        context.getConnectivityManager().unregisterNetworkCallback(this)
    }
}