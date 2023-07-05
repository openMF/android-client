package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.organisation.LoanProducts

/**
 * Created by Tarun on 25-07-2017.
 */
class CollectionSheetResponse() : Parcelable {
    var attendanceTypeOptions: List<AttendanceTypeOption> = ArrayList()
    var dueDate: IntArray? = null
    var groups: List<GroupCollectionSheet> = ArrayList()
    var loanProducts: List<LoanProducts> = ArrayList()
    var paymentTypeOptions: List<PaymentTypeOption> = ArrayList()
    var savingsProducts: List<SavingsProduct> = ArrayList()

    constructor(parcel: Parcel) : this() {
        attendanceTypeOptions = parcel.createTypedArrayList(AttendanceTypeOption.CREATOR) ?: ArrayList()
        dueDate = parcel.createIntArray()
        groups = parcel.createTypedArrayList(GroupCollectionSheet.CREATOR) ?: ArrayList()
        loanProducts = parcel.createTypedArrayList(LoanProducts.CREATOR) ?: ArrayList()
        paymentTypeOptions = parcel.createTypedArrayList(PaymentTypeOption.CREATOR) ?: ArrayList()
        savingsProducts = parcel.createTypedArrayList(SavingsProduct.CREATOR) ?: ArrayList()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(attendanceTypeOptions)
        dest.writeIntArray(dueDate)
        dest.writeTypedList(groups)
        dest.writeTypedList(loanProducts)
        dest.writeTypedList(paymentTypeOptions)
        dest.writeTypedList(savingsProducts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollectionSheetResponse> {
        override fun createFromParcel(parcel: Parcel): CollectionSheetResponse {
            return CollectionSheetResponse(parcel)
        }

        override fun newArray(size: Int): Array<CollectionSheetResponse?> {
            return arrayOfNulls(size)
        }
    }
}