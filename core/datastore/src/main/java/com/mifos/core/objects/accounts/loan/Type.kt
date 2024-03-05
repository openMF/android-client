/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Type(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null,

    var disbursement: Boolean? = null,

    var repaymentAtDisbursement: Boolean? = null,

    var repayment: Boolean? = null,

    var contra: Boolean? = null,

    var waiveInterest: Boolean? = null,

    var waiveCharges: Boolean? = null,

    var accrual: Boolean? = null,

    var writeOff: Boolean? = null,

    var recoveryRepayment: Boolean? = null,

    var initiateTransfer: Boolean? = null,

    var approveTransfer: Boolean? = null,

    var withdrawTransfer: Boolean? = null,

    var rejectTransfer: Boolean? = null,

    var chargePayment: Boolean? = null,

    var refund: Boolean? = null
) : Parcelable