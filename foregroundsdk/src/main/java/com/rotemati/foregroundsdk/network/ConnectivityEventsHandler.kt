package com.rotemati.foregroundsdk.network

import android.content.Context
import com.rotemati.foregroundsdk.extensions.getConnectivityManager

interface ConnectivityEventsHandler {
	fun register()

	fun unregister()

	var hasInternetAccess: Boolean

	var isBlocked: Boolean

	fun isConnected(context: Context): Boolean {
		val networkInfo = context.getConnectivityManager().activeNetworkInfo
		// todo check context.getConnectivityManager().activeNetwork
		return networkInfo != null && networkInfo.isConnected
	}

	fun getDetailedNetworkState(context: Context): String? {
		val networkInfo = context.getConnectivityManager().activeNetworkInfo
		return networkInfo?.detailedState?.name ?: ""
	}
}