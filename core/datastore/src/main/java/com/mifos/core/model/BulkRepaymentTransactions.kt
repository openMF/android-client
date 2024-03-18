/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BulkRepaymentTransactions(

    var loanId: Int = 0,

    var transactionAmount: Double = 0.0,

    // Optional fields
    var accountNumber: String? = null,

    var bankNumber: String? = null,

    var checkNumber: String? = null,

    var paymentTypeId: Int? = null,

    var receiptNumber: String? = null,

    var routingCode: String? = null
) : Parcelable