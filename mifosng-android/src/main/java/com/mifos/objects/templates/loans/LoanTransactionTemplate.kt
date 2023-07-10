package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.mifos.objects.PaymentTypeOption
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 14/02/17.
 */
@Parcelize
data class LoanTransactionTemplate(
    var type: Type,
    var date: List<Int> = ArrayList(),
    var amount: Double,
    var manuallyReversed: Boolean,
    var possibleNextRepaymentDate: List<Int> = ArrayList(),
    var paymentTypeOptions: List<PaymentTypeOption> = ArrayList()
) : Parcelable