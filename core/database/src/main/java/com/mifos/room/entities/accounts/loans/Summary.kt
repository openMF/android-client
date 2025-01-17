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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.objects.account.loan.Currency

@Entity(tableName = "LoansAccountSummary")
data class Summary(
    @PrimaryKey
    var loanId: Int? = null,

    @Embedded
    var currency: Currency? = null,

    @ColumnInfo(name = "principalDisbursed")
    var principalDisbursed: Double? = null,

    @ColumnInfo(name = "principalPaid")
    var principalPaid: Double? = null,

    @ColumnInfo(name = "principalWrittenOff")
    var principalWrittenOff: Double? = null,

    @ColumnInfo(name = "principalOutstanding")
    var principalOutstanding: Double? = null,

    @ColumnInfo(name = "principalOverdue")
    var principalOverdue: Double? = null,

    @ColumnInfo(name = "interestCharged")
    var interestCharged: Double? = null,

    @ColumnInfo(name = "interestPaid")
    var interestPaid: Double? = null,

    @ColumnInfo(name = "interestWaived")
    var interestWaived: Double? = null,

    @ColumnInfo(name = "interestWrittenOff")
    var interestWrittenOff: Double? = null,

    @ColumnInfo(name = "interestOutstanding")
    var interestOutstanding: Double? = null,

    @ColumnInfo(name = "interestOverdue")
    var interestOverdue: Double? = null,

    @ColumnInfo(name = "feeChargesCharged")
    var feeChargesCharged: Double? = null,

    @ColumnInfo(name = "feeChargesDueAtDisbursementCharged")
    var feeChargesDueAtDisbursementCharged: Double? = null,

    @ColumnInfo(name = "feeChargesPaid")
    var feeChargesPaid: Double? = null,

    @ColumnInfo(name = "feeChargesWaived")
    var feeChargesWaived: Double? = null,

    @ColumnInfo(name = "feeChargesWrittenOff")
    var feeChargesWrittenOff: Double? = null,

    @ColumnInfo(name = "feeChargesOutstanding")
    var feeChargesOutstanding: Double? = null,

    @ColumnInfo(name = "feeChargesOverdue")
    var feeChargesOverdue: Double? = null,

    @ColumnInfo(name = "penaltyChargesCharged")
    var penaltyChargesCharged: Double? = null,

    @ColumnInfo(name = "penaltyChargesPaid")
    var penaltyChargesPaid: Double? = null,

    @ColumnInfo(name = "penaltyChargesWaived")
    var penaltyChargesWaived: Double? = null,

    @ColumnInfo(name = "penaltyChargesWrittenOff")
    var penaltyChargesWrittenOff: Double? = null,

    @ColumnInfo(name = "penaltyChargesOutstanding")
    var penaltyChargesOutstanding: Double? = null,

    @ColumnInfo(name = "penaltyChargesOverdue")
    var penaltyChargesOverdue: Double? = null,

    @ColumnInfo(name = "totalExpectedRepayment")
    var totalExpectedRepayment: Double? = null,

    @ColumnInfo(name = "totalRepayment")
    var totalRepayment: Double? = null,

    @ColumnInfo(name = "totalExpectedCostOfLoan")
    var totalExpectedCostOfLoan: Double? = null,

    @ColumnInfo(name = "totalCostOfLoan")
    var totalCostOfLoan: Double? = null,

    @ColumnInfo(name = "totalWaived")
    var totalWaived: Double? = null,

    @ColumnInfo(name = "totalWrittenOff")
    var totalWrittenOff: Double? = null,

    @ColumnInfo(name = "totalOutstanding")
    var totalOutstanding: Double? = null,

    @ColumnInfo(name = "totalOverdue")
    var totalOverdue: Double? = null,

    @ColumnInfo(name = "overdueSinceDate")
    var overdueSinceDate: List<Int>? = null,
)
