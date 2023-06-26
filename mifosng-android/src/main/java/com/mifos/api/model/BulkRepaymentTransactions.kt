/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.Gson

data class BulkRepaymentTransactions(
    var loanId: Int = 0,
    var transactionAmount: Double= 0.0,
    // Optional fields
    var accountNumber: String? = null,
    var bankNumber: String? = null,
    var checkNumber: String? = null,
    var paymentTypeId: Int? = null,
    var receiptNumber: String? = null,
    var routingCode: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(loanId)
        dest.writeDouble(transactionAmount)
        dest.writeString(accountNumber)
        dest.writeString(bankNumber)
        dest.writeString(checkNumber)
        dest.writeValue(paymentTypeId)
        dest.writeString(receiptNumber)
        dest.writeString(routingCode)
    }

    companion object CREATOR : Parcelable.Creator<BulkRepaymentTransactions> {
        override fun createFromParcel(parcel: Parcel): BulkRepaymentTransactions {
            return BulkRepaymentTransactions(parcel)
        }

        override fun newArray(size: Int): Array<BulkRepaymentTransactions?> {
            return arrayOfNulls(size)
        }
    }
}