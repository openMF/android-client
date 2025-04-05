/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "SavingsTransactionDate",
)
data class SavingsTransactionDateEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int? = null,
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
)
