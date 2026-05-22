/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.loan.entity

import kotlinx.serialization.Serializable
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "LoanAccountLoanType",
)
@Serializable
data class LoanTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
)
