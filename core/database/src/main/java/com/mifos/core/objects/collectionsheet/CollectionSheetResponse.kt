package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.organisation.LoanProducts
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

    var paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption> = ArrayList(),

    var savingsProducts: List<SavingsProduct> = ArrayList()
) : Parcelable