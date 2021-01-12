package com.rotemati.foregroundsdk.foregroundtask.internal.connectivity

import android.content.Context
import com.rotemati.foregroundsdk.foregroundtask.internal.extensions.getConnectivityManager

internal interface ConnectivityHandler {

	fun register(context: Context)

	fun unregister(context: Context)

	var hasInternetAccess: Boolean

	var isBlocked: Boolean

	fun isRoaming(context: Context): Boolean {
		val networkInfo = context.getConnectivityManager().activeNetworkInfo
		return networkInfo != null && networkInfo.isConnected && networkInfo.isRoaming
	}
}