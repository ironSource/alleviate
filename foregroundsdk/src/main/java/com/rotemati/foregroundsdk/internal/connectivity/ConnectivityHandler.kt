package com.rotemati.foregroundsdk.internal.connectivity

import android.content.Context
import com.rotemati.foregroundsdk.internal.extensions.getConnectivityManager

internal interface ConnectivityHandler {

	fun register(context: Context)

	fun unregister(context: Context)

	val connected: Boolean

	val blocked: Boolean

	val roaming: Boolean

	fun setConnectivityListener(listener: () -> Unit)

	fun isRoamingOld(context: Context): Boolean {
		val connectivityManager = context.getConnectivityManager()
		val networkInfo = connectivityManager.activeNetworkInfo
		return networkInfo != null && networkInfo.isConnected && networkInfo.isRoaming
	}
}