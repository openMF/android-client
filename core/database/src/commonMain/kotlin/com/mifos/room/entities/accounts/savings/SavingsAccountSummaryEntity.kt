/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import kotlinx.serialization.Serializable
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "SavingsAccountSummary",
)
@Serializable
data class SavingsAccountSummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val savingsId: Int? = null,

    val currency: SavingAccountCurrencyEntity? = null,

    val totalDeposits: Double? = null,

    val accountBalance: Double? = null,

    val totalWithdrawals: Double? = null,

    val totalInterestEarned: Double? = null,
)
