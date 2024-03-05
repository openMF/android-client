/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoanDisbursement(
    var actualDisbursementDate: String? = null,

    var note: String? = null,

    var transactionAmount: Double? = null,

    var paymentId: Int? = null,

    var locale: String? = "en",

    var dateFormat: String? = "dd MMMM yyyy"
) : Parcelable