/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LoanApproval (
    var approvedOnDate: String? = null,

    var approvedLoanAmount: String? = null,

    var expectedDisbursementDate: String? = null,

    var note: String? = null,

    var locale: String = "en",

    var dateFormat: String = "dd MMMM yyyy",
) : Parcelable