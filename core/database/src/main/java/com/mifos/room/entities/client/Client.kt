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
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.room.entities.Timeline
import com.mifos.room.entities.group.Group
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
    val id: Int = 0,

    @Transient
    val groupId: Int? = 0,

    val accountNo: String? = null,

    val clientId: Int? = null,

    val status: Status? = null,

    @Transient
    var sync: Boolean = false,

    val active: Boolean = false,

    val clientDate: ClientDate? = null,

    val activationDate: List<Int?> = emptyList(),

    val dobDate: List<Int?> = emptyList(),

    val groups: List<Group?> = emptyList(),

    val mobileNo: String? = null,

    val firstname: String? = null,

    val middlename: String? = null,

    val lastname: String? = null,

    val displayName: String? = null,

    val officeId: Int = 0,

    val officeName: String? = null,

    val staffId: Int = 0,

    val staffName: String? = null,

    val timeline: Timeline? = null,

    val fullname: String? = null,

    val imageId: Int = 0,

    val imagePresent: Boolean = false,

    val externalId: String? = null,
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
