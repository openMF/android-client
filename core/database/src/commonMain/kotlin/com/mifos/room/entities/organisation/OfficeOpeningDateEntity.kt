/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.organisation

import kotlinx.serialization.Serializable
import androidx.room3.ColumnInfo.Companion.UNSPECIFIED
import androidx.room3.ColumnInfo
import androidx.room3.ColumnInfo.Companion.INHERIT_FIELD_NAME
import androidx.room3.ColumnInfo.Companion.UNDEFINED
import androidx.room3.ColumnInfo.Companion.VALUE_UNSPECIFIED
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "OfficeOpeningDate",
)
@Serializable
data class OfficeOpeningDateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val officeId: Int,

    val year: Int? = null,

    val month: Int? = null,

    val day: Int? = null,
)
