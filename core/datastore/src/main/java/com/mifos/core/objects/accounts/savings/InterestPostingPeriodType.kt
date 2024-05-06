/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InterestPostingPeriodType(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable