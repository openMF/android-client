/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.group

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.room.entities.client.Status
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "Center",
    foreignKeys = [
        ForeignKey(
            entity = CenterDate::class,
            parentColumns = ["centerId"],
            childColumns = ["centerDate"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Center(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "sync")
    @Transient
    var sync: Boolean = false,

    @ColumnInfo(name = "accountNo")
    var accountNo: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "officeId")
    var officeId: Int? = null,

    @ColumnInfo(name = "officeName")
    var officeName: String? = null,

    @ColumnInfo(name = "staffId")
    var staffId: Int? = null,

    @ColumnInfo(name = "staffName")
    var staffName: String? = null,

    @ColumnInfo(name = "hierarchy")
    var hierarchy: String? = null,

    @ColumnInfo(name = "status")
    var status: Status? = null,

    @ColumnInfo(name = "active")
    var active: Boolean? = null,

    @ColumnInfo(name = "centerDate")
    @Transient
    var centerDate: CenterDate? = null,

    @ColumnInfo(name = "activationDate")
    var activationDate: String? = null,

    @ColumnInfo(name = "timeline")
    var timeline: String? = null,

    @ColumnInfo(name = "externalId")
    var externalId: String? = null,
) : Parcelable
