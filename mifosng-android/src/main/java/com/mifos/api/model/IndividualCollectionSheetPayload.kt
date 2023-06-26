package com.mifos.api.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by Tarun on 11-07-2017.
 */

data class IndividualCollectionSheetPayload(
    var bulkRepaymentTransactions: ArrayList<BulkRepaymentTransactions> = ArrayList(),
    var actualDisbursementDate: String? = null,
    var bulkDisbursementTransactions: List<BulkRepaymentTransactions> = ArrayList(),
    var bulkSavingsDueTransactions: List<BulkRepaymentTransactions> = ArrayList(),
    var dateFormat: String = "dd MMMM YYYY",
    var locale: String = "en",
    var transactionDate: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(BulkRepaymentTransactions.CREATOR) ?: ArrayList(),
        parcel.readString(),
        parcel.createTypedArrayList(BulkRepaymentTransactions.CREATOR) ?: ArrayList(),
        parcel.createTypedArrayList(BulkRepaymentTransactions.CREATOR) ?: ArrayList(),
        parcel.readString() ?: "dd MMMM YYYY",
        parcel.readString() ?: "en",
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(bulkRepaymentTransactions)
        dest.writeString(actualDisbursementDate)
        dest.writeTypedList(bulkDisbursementTransactions)
        dest.writeTypedList(bulkSavingsDueTransactions)
        dest.writeString(dateFormat)
        dest.writeString(locale)
        dest.writeString(transactionDate)
    }

    companion object CREATOR : Parcelable.Creator<IndividualCollectionSheetPayload> {
        override fun createFromParcel(parcel: Parcel): IndividualCollectionSheetPayload {
            return IndividualCollectionSheetPayload(parcel)
        }

        override fun newArray(size: Int): Array<IndividualCollectionSheetPayload?> {
            return arrayOfNulls(size)
        }
    }
}