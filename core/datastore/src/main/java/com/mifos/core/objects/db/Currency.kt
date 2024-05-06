/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    var code: String? = null,

    var name: String? = null,

    var decimalPlaces: Int = 0,

    var inMultiplesOf: Int = 0,

    var displaySymbol: String? = null,

    var nameCode: String? = null,

    var displayLabel: String? = null,

    var loan: Loan? = null
) : Parcelable