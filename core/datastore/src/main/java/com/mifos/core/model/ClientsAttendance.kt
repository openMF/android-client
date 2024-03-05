/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientsAttendance(

    var attendanceType: Int,

    var clientId: Int
) : Parcelable