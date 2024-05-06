/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionProcessingStrategy(
    var id: Int? = null,

    var code: String? = null,

    var name: String? = null
) : Parcelable