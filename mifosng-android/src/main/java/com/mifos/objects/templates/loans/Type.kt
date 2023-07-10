/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Type(
    var id: Int,
    var code: String,
    var value: String,
    var disbursement: Boolean,
    var repaymentAtDisbursement: Boolean,
    var repayment: Boolean,
    var contra: Boolean,
    var waiveInterest: Boolean,
    var waiveCharges: Boolean,
    var accrual: Boolean,
    var writeOff: Boolean,
    var recoveryRepayment: Boolean,
    var initiateTransfer: Boolean,
    var approveTransfer: Boolean,
    var withdrawTransfer: Boolean,
    var rejectTransfer: Boolean,
    var chargePayment: Boolean,
    var refund: Boolean
) : Parcelable