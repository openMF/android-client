/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.organisation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OfficeOpeningDate")
data class OfficeOpeningDateEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val officeId: Int,

    val year: Int? = null,

    val month: Int? = null,

    val day: Int? = null,
)
