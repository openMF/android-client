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
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(
    tableName = "GroupDate",
)
data class GroupDateEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val groupId: Long = 0,

    val chargeId: Long = 0,

    val day: Int = 0,

    val month: Int = 0,

    val year: Int = 0,
) : Parcelable
