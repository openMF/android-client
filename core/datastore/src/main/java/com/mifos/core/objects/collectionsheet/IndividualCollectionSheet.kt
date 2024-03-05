package com.mifos.core.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions

/**
 * Created by Tarun on 06-07-2017.
 */
class IndividualCollectionSheet() : Parcelable {
    var dueDate: IntArray? = null

    @SerializedName("clients")
    var clients: ArrayList<ClientCollectionSheet>? = null

    var paymentTypeOptions: ArrayList<PaymentTypeOptions>? = null

    constructor(parcel: Parcel) : this() {
        dueDate = parcel.createIntArray()
        clients = ArrayList<ClientCollectionSheet>().apply {
            parcel.readList(this, ClientCollectionSheet::class.java.classLoader)
        }
        paymentTypeOptions = ArrayList<PaymentTypeOptions>().apply {
            parcel.readList(this, PaymentTypeOptions::class.java.classLoader)
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeIntArray(dueDate)
        dest.writeList(clients)
        dest.writeList(paymentTypeOptions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IndividualCollectionSheet> {
        override fun createFromParcel(parcel: Parcel): IndividualCollectionSheet {
            return IndividualCollectionSheet(parcel)
        }

        override fun newArray(size: Int): Array<IndividualCollectionSheet?> {
            return arrayOfNulls(size)
        }
    }
}