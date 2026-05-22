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
import kotlinx.serialization.Serializable

@Entity(
    tableName = "LoanStatus",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
@Serializable
data class LoanStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val code: String? = null,

    val value: String? = null,

    val pendingApproval: Boolean? = null,

    val waitingForDisbursal: Boolean? = null,

    val active: Boolean? = null,

    val closedObligationsMet: Boolean? = null,

    val closedWrittenOff: Boolean? = null,

    val closedRescheduled: Boolean? = null,

    val closed: Boolean? = null,

    val overpaid: Boolean? = null,
)
