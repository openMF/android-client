/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 23/06/14.
 */
@Parcelize
data class LoanApprovalRequest(
    var locale: String = "en",

    var dateFormat: String = "dd MM yyyy",

    var approvedOnDate: String? = null,

    var note: String? = null
) : Parcelable