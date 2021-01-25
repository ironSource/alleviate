package com.rotemati.foregroundsdk.internal.connectivity

import android.content.Context

internal class GetConnectivityState(
		private val context: Context,
		private val connectivityHandler: ConnectivityHandler
) {
	operator fun invoke(): ConnectivityState {
		if (connectivityHandler.isBlocked) {
			return ConnectivityState.Blocked
		}
		if (!connectivityHandler.hasInternetAccess) {
			return ConnectivityState.NotConnected
		}
		return when (connectivityHandler.isRoaming(context)) {
			true -> ConnectivityState.Roaming
			false -> ConnectivityState.Connected
		}
	}
}

internal sealed class ConnectivityState {
	object Blocked : ConnectivityState()
	object NotConnected : ConnectivityState()
	object Roaming : ConnectivityState()
	object Connected : ConnectivityState()
}