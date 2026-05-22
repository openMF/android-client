/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.center.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = "CenterDate",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
data class CenterDateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val centerId: Long = 0,

    val chargeId: Long = 0,

    val day: Int = 0,

    val month: Int = 0,

    val year: Int = 0,
)
