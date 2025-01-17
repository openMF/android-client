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
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "GroupDate",
    primaryKeys = ["groupId", "chargeId"],
)
data class GroupDate(
    @ColumnInfo(name = "groupId")
    var groupId: Long = 0,

    @ColumnInfo(name = "chargeId")
    var chargeId: Long = 0,

    @ColumnInfo(name = "day")
    var day: Int = 0,

    @ColumnInfo(name = "month")
    var month: Int = 0,

    @ColumnInfo(name = "year")
    var year: Int = 0,
) : Parcelable
