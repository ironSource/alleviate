package com.rotemati.foregroundsdk.jobinfo

import android.app.Notification
import android.os.Parcel
import android.os.Parcelable

data class ForegroundJobInfo(
    val id: Int,
    val networkType: Int,
    val isPersisted: Boolean,
    val minLatencyMillis: Long,
    val notification: Notification?,
    val timeout: Long,
    val rescheduleOnFail: Boolean,
    val maxRetries: Int = 5,
    val retryCount: Int = 0
//    val foregroundObtainer: ForegroundObtainer
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readParcelable(Notification::class.java.classLoader),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
//        parcel.readSerializable()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(networkType)
        parcel.writeByte(if (isPersisted) 1 else 0)
        parcel.writeLong(minLatencyMillis)
        parcel.writeParcelable(notification, flags)
        parcel.writeLong(timeout)
        parcel.writeByte(if (rescheduleOnFail) 1 else 0)
        parcel.writeInt(maxRetries)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ForegroundJobInfo> {
        override fun createFromParcel(parcel: Parcel): ForegroundJobInfo {
            return ForegroundJobInfo(parcel)
        }

        override fun newArray(size: Int): Array<ForegroundJobInfo?> {
            return arrayOfNulls(size)
        }
    }
}

fun ForegroundJobInfo.latencyEpoch() = System.currentTimeMillis() + minLatencyMillis