/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by ishankhanna on 09/02/14.
 */
class Timeline() : BaseModel(), Parcelable {
    var submittedOnDate: MutableList<Int> = ArrayList()
    var submittedByUsername: String? = null
    var submittedByFirstname: String? = null
    var submittedByLastname: String? = null
    var activatedOnDate: MutableList<Int> = ArrayList()
    var activatedByUsername: String? = null
    var activatedByFirstname: String? = null
    var activatedByLastname: String? = null
    var closedOnDate: MutableList<Int> = ArrayList()
    var closedByUsername: String? = null
    var closedByFirstname: String? = null
    var closedByLastname: String? = null

    constructor(parcel: Parcel) : this() {
        parcel.readList(submittedOnDate, Int::class.java.classLoader)
        submittedByUsername = parcel.readString()
        submittedByFirstname = parcel.readString()
        submittedByLastname = parcel.readString()
        parcel.readList(activatedOnDate, Int::class.java.classLoader)
        activatedByUsername = parcel.readString()
        activatedByFirstname = parcel.readString()
        activatedByLastname = parcel.readString()
        parcel.readList(closedOnDate, Int::class.java.classLoader)
        closedByUsername = parcel.readString()
        closedByFirstname = parcel.readString()
        closedByLastname = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(submittedOnDate)
        parcel.writeString(submittedByUsername)
        parcel.writeString(submittedByFirstname)
        parcel.writeString(submittedByLastname)
        parcel.writeList(activatedOnDate)
        parcel.writeString(activatedByUsername)
        parcel.writeString(activatedByFirstname)
        parcel.writeString(activatedByLastname)
        parcel.writeList(closedOnDate)
        parcel.writeString(closedByUsername)
        parcel.writeString(closedByFirstname)
        parcel.writeString(closedByLastname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Timeline> {
        override fun createFromParcel(parcel: Parcel): Timeline {
            return Timeline(parcel)
        }

        override fun newArray(size: Int): Array<Timeline?> {
            return arrayOfNulls(size)
        }
    }
}