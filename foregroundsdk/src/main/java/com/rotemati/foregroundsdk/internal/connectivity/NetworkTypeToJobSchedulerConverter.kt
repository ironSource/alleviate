package com.rotemati.foregroundsdk.internal.connectivity

import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType

private const val NETWORK_TYPE_NONE = 0
private const val NETWORK_TYPE_ANY = 1
private const val NETWORK_TYPE_NOT_ROAMING = 3

internal class NetworkTypeToJobSchedulerConverter {

    fun convert(networkType: NetworkType): Int {
        return when (networkType) {
			NetworkType.None -> NETWORK_TYPE_NONE
			NetworkType.Any -> NETWORK_TYPE_ANY
			NetworkType.NotRoaming -> NETWORK_TYPE_NOT_ROAMING
        }
    }
}