package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by Tarun on 25-07-2017.
 */
class CollectionSheetRequestPayload() : Parcelable {
    var calendarId: Int = 0
    var dateFormat: String = "dd MMMM yyyy"
    var locale: String = "en"
    var transactionDate: String? = null

    constructor(parcel: Parcel) : this() {
        calendarId = parcel.readInt()
        dateFormat = parcel.readString() ?: ""
        locale = parcel.readString() ?: ""
        transactionDate = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(calendarId)
        dest.writeString(dateFormat)
        dest.writeString(locale)
        dest.writeString(transactionDate)
    }

    override fun toString(): String {
        return "CollectionSheetRequestPayload(calendarId=$calendarId, dateFormat='$dateFormat', locale='$locale', transactionDate=$transactionDate)"
    }

    companion object CREATOR : Parcelable.Creator<CollectionSheetRequestPayload> {
        override fun createFromParcel(parcel: Parcel): CollectionSheetRequestPayload {
            return CollectionSheetRequestPayload(parcel)
        }

        override fun newArray(size: Int): Array<CollectionSheetRequestPayload?> {
            return arrayOfNulls(size)
        }
    }
}