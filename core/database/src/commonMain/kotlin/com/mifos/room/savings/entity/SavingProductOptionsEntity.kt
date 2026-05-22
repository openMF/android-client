/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.savings.entity

import kotlinx.serialization.Serializable
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "ClientTemplateSavingProductsOptions",
)
data class SavingProductOptionsEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val name: String = "",

    val withdrawalFeeForTransfers: Boolean = false,

    val allowOverdraft: Boolean = false,
)
