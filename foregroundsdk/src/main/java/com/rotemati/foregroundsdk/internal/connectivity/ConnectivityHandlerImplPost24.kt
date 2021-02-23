package com.rotemati.foregroundsdk.internal.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.rotemati.foregroundsdk.external.ForegroundSdk
import com.rotemati.foregroundsdk.external.logger.ForegroundLogger
import com.rotemati.foregroundsdk.internal.extensions.getConnectivityManager
import com.rotemati.foregroundsdk.internal.logger.LoggerWrapper

@RequiresApi(Build.VERSION_CODES.N)
internal class ConnectivityHandlerImplPost24 : ConnectivityManager.NetworkCallback(), ConnectivityHandler {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSdk.logger)
	private var onConnectivityChanged: ConnectivityChangedListener? = null

	override val connected: Boolean
		get() = isConnected(ForegroundSdk.context)

	override val roaming: Boolean
		get() = isRoaming(ForegroundSdk.context)

	override var blocked = false

	override fun setConnectivityListener(listener: ConnectivityChangedListener) {
		this.onConnectivityChanged = listener
	}

	override fun onLost(network: Network) {
		super.onLost(network)
		onConnectivityChanged?.onChanged()
	}

	override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
		super.onCapabilitiesChanged(network, networkCapabilities)
		onConnectivityChanged?.onChanged()
	}

	override fun onUnavailable() {
		super.onUnavailable()
		onConnectivityChanged?.onChanged()
	}

	override fun onLosing(network: Network, maxMsToLive: Int) {
		super.onLosing(network, maxMsToLive)
		onConnectivityChanged?.onChanged()
	}

	override fun onAvailable(network: Network) {
		super.onAvailable(network)
		onConnectivityChanged?.onChanged()
	}

	override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
		super.onBlockedStatusChanged(network, blocked)
		this.blocked = blocked
		onConnectivityChanged?.onChanged()
	}

	override fun register(context: Context) {
		context.getConnectivityManager().registerDefaultNetworkCallback(this)
	}

	override fun unregister(context: Context) {
		context.getConnectivityManager().unregisterNetworkCallback(this)
	}

	private fun getNetworkCapabilities(context: Context): NetworkCapabilities? {
		val connectivityManager = context.getConnectivityManager()
		val activeNetwork = connectivityManager.activeNetwork
		if (activeNetwork == null) {
			logger.i("No active network found")
			// There's no active network
			return null
		}
		return connectivityManager.getNetworkCapabilities(activeNetwork)
	}

	private fun isConnected(context: Context): Boolean {
		return getNetworkCapabilities(context)?.let {
			it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
		} ?: false
	}

	private fun isRoaming(context: Context): Boolean {
		val networkCapabilities = getNetworkCapabilities(context) ?: return false // Network capabilities not found
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			isConnected(context) && !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
		} else {
			isRoamingOld(context)
		}
	}
}