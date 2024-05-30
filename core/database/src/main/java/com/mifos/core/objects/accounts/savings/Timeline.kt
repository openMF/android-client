/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Timeline(
    var submittedOnDate: List<Int?> = ArrayList(),

    var submittedByUsername: String? = null,

    var submittedByFirstname: String? = null,

    var submittedByLastname: String? = null,

    var approvedOnDate: List<Int?> = ArrayList(),

    var approvedByUsername: String? = null,

    var approvedByFirstname: String? = null,

    var approvedByLastname: String? = null,

    var activatedOnDate: List<Int?> = ArrayList()
) : Parcelable