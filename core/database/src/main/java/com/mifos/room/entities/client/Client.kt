/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.core.entity.group.Group
import com.mifos.room.entities.Timeline
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "Client",
    foreignKeys = [
        ForeignKey(
            entity = Status::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClientDate::class,
            parentColumns = ["clientId"],
            childColumns = ["clientDate"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Client(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "groupId")
    @Transient
    var groupId: Int? = 0,

    @ColumnInfo(name = "accountNo")
    var accountNo: String? = null,

    @ColumnInfo(name = "clientId")
    var clientId: Int? = null,

    @ColumnInfo(name = "status")
    var status: Status? = null,

    @ColumnInfo(name = "sync")
    @Transient
    var sync: Boolean = false,

    @ColumnInfo(name = "active")
    var active: Boolean = false,

    @ColumnInfo(name = "clientDate")
    var clientDate: ClientDate? = null,

    @ColumnInfo(name = "activationDate")
    var activationDate: List<Int?> = ArrayList(),

    @ColumnInfo(name = "dobDate")
    var dobDate: List<Int?> = ArrayList(),

    @ColumnInfo(name = "groups")
    var groups: List<Group?> = ArrayList(),

    @ColumnInfo(name = "mobileNo")
    var mobileNo: String? = null,

    @ColumnInfo(name = "firstname")
    var firstname: String? = null,

    @ColumnInfo(name = "middlename")
    var middlename: String? = null,

    @ColumnInfo(name = "lastname")
    var lastname: String? = null,

    @ColumnInfo(name = "displayName")
    var displayName: String? = null,

    @ColumnInfo(name = "officeId")
    var officeId: Int = 0,

    @ColumnInfo(name = "officeName")
    var officeName: String? = null,

    @ColumnInfo(name = "staffId")
    var staffId: Int = 0,

    @ColumnInfo(name = "staffName")
    var staffName: String? = null,

    @ColumnInfo(name = "timeline")
    var timeline: Timeline? = null,

    @ColumnInfo(name = "fullname")
    var fullname: String? = null,

    @ColumnInfo(name = "imageId")
    var imageId: Int = 0,

    @ColumnInfo(name = "imagePresent")
    var imagePresent: Boolean = false,

    @ColumnInfo(name = "externalId")
    var externalId: String? = null,
) : Parcelable {

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
