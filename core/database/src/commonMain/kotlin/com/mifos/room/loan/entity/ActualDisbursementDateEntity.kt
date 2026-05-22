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

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = "ActualDisbursementDateEntity",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
data class ActualDisbursementDateEntity(
    @PrimaryKey(autoGenerate = true)
    val loanId: Int? = null,
    val year: Int? = null,
    val month: Int? = null,
    val date: Int? = null,
)
