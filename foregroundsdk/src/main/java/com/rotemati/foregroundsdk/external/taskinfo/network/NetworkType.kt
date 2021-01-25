package com.rotemati.foregroundsdk.external.taskinfo.network

import java.io.Serializable

enum class NetworkType : Serializable {
	None,
	Any,
	NotRoaming
}


const val NETWORK_TYPE_NONE = 0

const val NETWORK_TYPE_ANY = 1

const val NETWORK_TYPE_NOT_ROAMING = 3