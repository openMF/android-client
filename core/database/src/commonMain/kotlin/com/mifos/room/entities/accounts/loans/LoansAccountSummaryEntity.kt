/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "LoansAccountSummary",
)
@Serializable
@Parcelize
data class LoansAccountSummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val loanId: Int? = null,

    @IgnoredOnParcel
    val currency: SavingAccountCurrencyEntity? = null,

    val principalDisbursed: Double? = null,

    val principalPaid: Double? = null,

    val principalWrittenOff: Double? = null,

    val principalOutstanding: Double? = null,

    val principalOverdue: Double? = null,

    val interestCharged: Double? = null,

    val interestPaid: Double? = null,

    val interestWaived: Double? = null,

    val interestWrittenOff: Double? = null,

    val interestOutstanding: Double? = null,

    val interestOverdue: Double? = null,

    val feeChargesCharged: Double? = null,

    val feeChargesDueAtDisbursementCharged: Double? = null,

    val feeChargesPaid: Double? = null,

    val feeChargesWaived: Double? = null,

    val feeChargesWrittenOff: Double? = null,

    val feeChargesOutstanding: Double? = null,

    val feeChargesOverdue: Double? = null,

    val penaltyChargesCharged: Double? = null,

    val penaltyChargesPaid: Double? = null,

    val penaltyChargesWaived: Double? = null,

    val penaltyChargesWrittenOff: Double? = null,

    val penaltyChargesOutstanding: Double? = null,

    val penaltyChargesOverdue: Double? = null,

    val totalExpectedRepayment: Double? = null,

    val totalRepayment: Double? = null,

    val totalExpectedCostOfLoan: Double? = null,

    val totalCostOfLoan: Double? = null,

    val totalWaived: Double? = null,

    val totalWrittenOff: Double? = null,

    val totalOutstanding: Double? = null,

    val totalOverdue: Double? = null,

    val overdueSinceDate: List<Int>? = null,
) : Parcelable
