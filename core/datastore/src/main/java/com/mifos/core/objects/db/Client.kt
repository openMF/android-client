/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    var clientId: Int = 0,

    var clientName: String? = null,

    var attendanceType: AttendanceType? = null,

    var mifosGroup: MifosGroup? = null,

    val loans: List<Loan> = ArrayList()
) : Parcelable