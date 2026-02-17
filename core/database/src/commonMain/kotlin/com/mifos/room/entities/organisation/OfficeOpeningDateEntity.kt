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

import kotlinx.serialization.Serializable
import template.core.base.database.CollationSequence.UNSPECIFIED
import template.core.base.database.ColumnInfo
import template.core.base.database.ColumnInfoTypeAffinity.INHERIT_FIELD_NAME
import template.core.base.database.ColumnInfoTypeAffinity.UNDEFINED
import template.core.base.database.ColumnInfoTypeAffinity.VALUE_UNSPECIFIED
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

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
