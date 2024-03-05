/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.mifos.core.objects.Changes
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoanRepaymentResponse (
    @Expose
    var officeId: Int? = null,

    @Expose
    var clientId: Int? = null,

    @Expose
    var loanId: Int? = null,

    @Expose
    var resourceId: Int? = null,

    @Expose
    var changes: com.mifos.core.objects.Changes? = null
) : Parcelable