/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.group.Group
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 08/02/14.
 */
@Parcelize
@Entity("Client")
data class Client(
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo("groupId")
    @Transient
    var groupId: Int? = 0,

    @ColumnInfo("accountNo")
    var accountNo: String? = null,

    var clientId: Int? = null,

    @ColumnInfo("status")
    @Embedded
    var status: Status? = null,

    @ColumnInfo("sync")
    @Transient
    var sync: Boolean = false,

    @ColumnInfo("active")
    var active: Boolean = false,

    @ColumnInfo("clientTypeId")
    @Embedded
    var clientDate: ClientDate? = null,

    var activationDate: List<Int?> = ArrayList(),

    var dobDate: List<Int?> = ArrayList(),

    var groups: List<Group?> = ArrayList(),

    var mobileNo: String? = null,

    @ColumnInfo("firstname")
    var firstname: String? = null,

    @ColumnInfo("middlename")
    var middlename: String? = null,

    @ColumnInfo("lastname")
    var lastname: String? = null,

    @ColumnInfo("displayName")
    var displayName: String? = null,

    @ColumnInfo("officeId")
    var officeId: Int = 0,

    @ColumnInfo("officeName")
    var officeName: String? = null,

    @ColumnInfo("staffId")
    var staffId: Int = 0,

    @ColumnInfo("staffName")
    var staffName: String? = null,

    var timeline: Timeline? = null,

    @ColumnInfo("fullname")
    var fullname: String? = null,

    @ColumnInfo("imageId")
    var imageId: Int = 0,

    @ColumnInfo("imagePresent")
    var imagePresent: Boolean = false,

    @ColumnInfo("externalId")
    var externalId: String? = null,
) : MifosBaseModel(), Parcelable {

    /**
     * This method is returning the comma separated names of all the client's group
     * in the form of a string.
     */
    val groupNames: String
        get() {
            var groupNames = ""
            if (groups.isEmpty()) return ""
            for (group in groups) {
                groupNames += group!!.name + ", "
            }
            return groupNames.substring(0, groupNames.length - 2)
        }
}
