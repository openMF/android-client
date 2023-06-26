package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.objects.collectionsheet.LoanCollectionSheet

/**
 * Created by Tarun on 17-07-2017.
 */
class LoanAndClientName(
    val loan: LoanCollectionSheet?,
    val clientName: String?,
    val id: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(LoanCollectionSheet::class.java.classLoader),
        parcel.readString(),
        parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(loan, flags)
        dest.writeString(clientName)
        dest.writeInt(id)
    }

    override fun toString(): String {
        return "LoanAndClientName(loan=$loan, clientName='$clientName', id=$id)"
    }

    companion object CREATOR : Parcelable.Creator<LoanAndClientName> {
        override fun createFromParcel(parcel: Parcel): LoanAndClientName {
            return LoanAndClientName(parcel)
        }

        override fun newArray(size: Int): Array<LoanAndClientName?> {
            return arrayOfNulls(size)
        }
    }
}