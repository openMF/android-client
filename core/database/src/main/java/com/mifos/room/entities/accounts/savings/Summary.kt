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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Transient

@Entity(tableName = "SavingsAccountSummary")
// @TypeConverters(CurrencyTypeConverter::class)
data class Summary(
    @PrimaryKey
    @Transient
    val savingsId: Int? = null,

    @Embedded
    val currency: Currency? = null,

    val totalDeposits: Double? = null,

    val accountBalance: Double? = null,

    val totalWithdrawals: Double? = null,

    val totalInterestEarned: Double? = null,
)
