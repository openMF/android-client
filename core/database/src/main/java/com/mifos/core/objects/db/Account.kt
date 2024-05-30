/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    var accountId: String? = null,

    var accountStatusId: Int = 0,

    var loanId: Int = 0
) : Parcelable