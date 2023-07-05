package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.api.model.ClientsAttendance

/**
 * Created by Tarun on 31-07-17.
 */
class CollectionSheetPayload() : Parcelable {
    var actualDisbursementDate: String? = null
    var bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList()
    var bulkSavingsDueTransactions: MutableList<BulkSavingsDueTransaction> = ArrayList()
    var calendarId: Int = 0
    var clientsAttendance: MutableList<ClientsAttendance> = ArrayList()
    var dateFormat: String = "dd MMMM yyyy"
    var locale: String = "en"
    var transactionDate: String? = null
    var accountNumber: String? = null
    var bankNumber: String? = null
    var checkNumber: String? = null
    var paymentTypeId: Int = 0
    var receiptNumber: String? = null
    var routingCode: String? = null

    constructor(parcel: Parcel) : this() {
        actualDisbursementDate = parcel.readString()
        parcel.readTypedList(bulkRepaymentTransactions, BulkRepaymentTransactions.CREATOR)
        parcel.readTypedList(bulkSavingsDueTransactions, BulkSavingsDueTransaction.CREATOR)
        calendarId = parcel.readInt()
        parcel.readTypedList(clientsAttendance, ClientsAttendance.CREATOR)
        dateFormat = parcel.readString()!!
        locale = parcel.readString()!!
        transactionDate = parcel.readString()
        accountNumber = parcel.readString()
        bankNumber = parcel.readString()
        checkNumber = parcel.readString()
        paymentTypeId = parcel.readInt()
        receiptNumber = parcel.readString()
        routingCode = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(actualDisbursementDate)
        parcel.writeTypedList(bulkRepaymentTransactions)
        parcel.writeTypedList(bulkSavingsDueTransactions)
        parcel.writeInt(calendarId)
        parcel.writeTypedList(clientsAttendance)
        parcel.writeString(dateFormat)
        parcel.writeString(locale)
        parcel.writeString(transactionDate)
        parcel.writeString(accountNumber)
        parcel.writeString(bankNumber)
        parcel.writeString(checkNumber)
        parcel.writeInt(paymentTypeId)
        parcel.writeString(receiptNumber)
        parcel.writeString(routingCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollectionSheetPayload> {
        override fun createFromParcel(parcel: Parcel): CollectionSheetPayload {
            return CollectionSheetPayload(parcel)
        }

        override fun newArray(size: Int): Array<CollectionSheetPayload?> {
            return arrayOfNulls(size)
        }
    }
}