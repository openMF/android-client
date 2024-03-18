package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import com.mifos.core.objects.PaymentTypeOption
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 14/02/17.
 */
@Parcelize
data class LoanTransactionTemplate(
    var type: Type? = null,

    var date: List<Int> = ArrayList(),

    var amount: Double? = null,

    var manuallyReversed: Boolean? = null,

    var possibleNextRepaymentDate: List<Int> = ArrayList(),

    var paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption> = ArrayList()
) : Parcelable