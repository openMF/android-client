/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MifosGroup(
    var staffId: Int = 0,

    var staffName: String? = null,

    var levelId: Int = 0,

    var levelName: String? = null,

    var groupId: Long = 0,

    var groupName: String? = null,

    var centerId: Long = 0,

    var clients: List<Client> = ArrayList()
) : Parcelable