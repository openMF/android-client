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

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.ColumnInfo
import com.mifos.room.utils.Entity
import com.mifos.room.utils.INHERIT_FIELD_NAME
import com.mifos.room.utils.PrimaryKey
import com.mifos.room.utils.UNDEFINED
import com.mifos.room.utils.UNSPECIFIED
import com.mifos.room.utils.VALUE_UNSPECIFIED
import kotlinx.serialization.Serializable

@Parcelize
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "ClientDate",
)
@Serializable
data class ClientDateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val clientId: Int = 0,

    val chargeId: Long = 0,

    val day: Int = 0,

    val month: Int = 0,

    val year: Int = 0,
) : Parcelable
