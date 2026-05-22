/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.charge.entity

import kotlinx.serialization.Serializable
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "ClientChargeCurrency",
)
data class ClientChargeCurrencyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val id: Int = 0,

    val code: String? = null,

    val name: String? = null,

    val decimalPlaces: Int? = null,

    val inMultiplesOf: Int? = null,

    val displaySymbol: String? = null,

    val nameCode: String? = null,

    val displayLabel: String? = null,
)
