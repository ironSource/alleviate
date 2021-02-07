package com.rotemati.foregroundsdk.internal.connectivity

internal class GetConnectivityState(
	private val connectivityHandler: ConnectivityHandler
) {
    operator fun invoke(): ConnectivityState {
        if (connectivityHandler.blocked) {
            return ConnectivityState.Blocked
        }
        if (!connectivityHandler.connected) {
            return ConnectivityState.NotConnected
        }
        return when (connectivityHandler.roaming) {
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