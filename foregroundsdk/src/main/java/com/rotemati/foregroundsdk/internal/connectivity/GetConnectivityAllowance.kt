package com.rotemati.foregroundsdk.internal.connectivity

import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType

internal class GetConnectivityAllowance {

	operator fun invoke(networkType: NetworkType, connectivityState: ConnectivityState): ConnectivityAllowance {
		if (networkType == NetworkType.None) {
			return ConnectivityAllowance.Allowed
		} else {
			if (networkType == NetworkType.NotRoaming && connectivityState is ConnectivityState.Roaming) {
				return ConnectivityAllowance.NotAllowed("Task requires not roaming but in roaming")
			} else {
				if (connectivityState is ConnectivityState.Blocked) {
					return ConnectivityAllowance.NotAllowed("Task requires network but network is blocked")
				}
				if (connectivityState is ConnectivityState.NotConnected) {
					return ConnectivityAllowance.NotAllowed("Task Requires network but no internet connection")
				}
			}
			return ConnectivityAllowance.Allowed
		}
	}
}

internal sealed class ConnectivityAllowance {
	object Allowed : ConnectivityAllowance()
	data class NotAllowed(val reason: String) : ConnectivityAllowance()
}