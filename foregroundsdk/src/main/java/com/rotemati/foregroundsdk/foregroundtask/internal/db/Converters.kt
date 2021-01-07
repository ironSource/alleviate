package com.rotemati.foregroundsdk.foregroundtask.internal.db

import androidx.room.TypeConverter
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType

class Converters {
	@TypeConverter
	fun toNetworkType(value: Int) = enumValues<NetworkType>()[value]

	@TypeConverter
	fun fromNetworkType(value: NetworkType) = value.ordinal
}