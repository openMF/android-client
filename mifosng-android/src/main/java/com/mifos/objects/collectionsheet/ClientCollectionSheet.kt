package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import com.mifos.objects.collectionsheet.LoanCollectionSheet

/**
 * Created by Tarun on 06-07-2017.
 */
class ClientCollectionSheet : Parcelable {
    var clientId = 0
    var clientName: String? = null

    @SerializedName("loans")
    var loans: ArrayList<LoanCollectionSheet>? = null
    var attendanceType: AttendanceTypeOption? = null
    var savings: ArrayList<SavingsCollectionSheet>? = ArrayList()
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(clientId)
        dest.writeString(clientName)
        dest.writeParcelable(attendanceType, flags)
        dest.writeTypedList(savings)
        dest.writeList(loans)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        clientId = `in`.readInt()
        clientName = `in`.readString()
        attendanceType = `in`.readParcelable(AttendanceTypeOption::class.java.classLoader)
        savings = `in`.createTypedArrayList(SavingsCollectionSheet.CREATOR)
        loans = ArrayList()
        `in`.readList(loans!!, LoanCollectionSheet::class.java.classLoader)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<ClientCollectionSheet> = object : Creator<ClientCollectionSheet> {
            override fun createFromParcel(source: Parcel): ClientCollectionSheet {
                return ClientCollectionSheet(source)
            }

            override fun newArray(size: Int): Array<ClientCollectionSheet?> {
                return arrayOfNulls(size)
            }
        }
    }
}