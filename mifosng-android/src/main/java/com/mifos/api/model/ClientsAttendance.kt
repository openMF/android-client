/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.Gson

data class ClientsAttendance(
    var attendanceType: Int,
    var clientId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(attendanceType)
        dest.writeInt(clientId)
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object CREATOR : Parcelable.Creator<ClientsAttendance> {
        override fun createFromParcel(parcel: Parcel): ClientsAttendance {
            return ClientsAttendance(parcel)
        }

        override fun newArray(size: Int): Array<ClientsAttendance?> {
            return arrayOfNulls(size)
        }
    }
}