package com.rotemati.foregroundtesterapp

import android.os.Parcel
import android.os.Parcelable

data class TestUser(val id: String?, val name: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TestUser> {
        override fun createFromParcel(parcel: Parcel): TestUser {
            return TestUser(parcel)
        }

        override fun newArray(size: Int): Array<TestUser?> {
            return arrayOfNulls(size)
        }
    }
}