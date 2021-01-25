package com.rotemati.foregroundsdk.internal.db

import androidx.room.TypeConverter
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType

internal class Converters {
	@TypeConverter
	fun toNetworkType(value: Int) = enumValues<NetworkType>()[value]

	@TypeConverter
	fun fromNetworkType(value: NetworkType) = value.ordinal
}