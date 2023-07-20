package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.organisation.LoanProducts
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class CollectionSheetResponse(
    var attendanceTypeOptions: List<AttendanceTypeOption> = ArrayList(),

    var dueDate: IntArray? = null,

    var groups: List<GroupCollectionSheet> = ArrayList(),

    var loanProducts: List<LoanProducts> = ArrayList(),

    var paymentTypeOptions: List<PaymentTypeOption> = ArrayList(),

    var savingsProducts: List<SavingsProduct> = ArrayList()
    ) : Parcelable