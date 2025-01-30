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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.objects.account.loan.Currency
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "LoansAccountSummary")
@Serializable
@Parcelize
data class Summary(
    @PrimaryKey
    val loanId: Int? = null,

    @Embedded
    val currency: Currency? = null,

    @ColumnInfo(name = "principalDisbursed")
    val principalDisbursed: Double? = null,

    @ColumnInfo(name = "principalPaid")
    val principalPaid: Double? = null,

    @ColumnInfo(name = "principalWrittenOff")
    val principalWrittenOff: Double? = null,

    @ColumnInfo(name = "principalOutstanding")
    val principalOutstanding: Double? = null,

    @ColumnInfo(name = "principalOverdue")
    val principalOverdue: Double? = null,

    @ColumnInfo(name = "interestCharged")
    val interestCharged: Double? = null,

    @ColumnInfo(name = "interestPaid")
    val interestPaid: Double? = null,

    @ColumnInfo(name = "interestWaived")
    val interestWaived: Double? = null,

    @ColumnInfo(name = "interestWrittenOff")
    val interestWrittenOff: Double? = null,

    @ColumnInfo(name = "interestOutstanding")
    val interestOutstanding: Double? = null,

    @ColumnInfo(name = "interestOverdue")
    val interestOverdue: Double? = null,

    @ColumnInfo(name = "feeChargesCharged")
    val feeChargesCharged: Double? = null,

    @ColumnInfo(name = "feeChargesDueAtDisbursementCharged")
    val feeChargesDueAtDisbursementCharged: Double? = null,

    @ColumnInfo(name = "feeChargesPaid")
    val feeChargesPaid: Double? = null,

    @ColumnInfo(name = "feeChargesWaived")
    val feeChargesWaived: Double? = null,

    @ColumnInfo(name = "feeChargesWrittenOff")
    val feeChargesWrittenOff: Double? = null,

    @ColumnInfo(name = "feeChargesOutstanding")
    val feeChargesOutstanding: Double? = null,

    @ColumnInfo(name = "feeChargesOverdue")
    val feeChargesOverdue: Double? = null,

    @ColumnInfo(name = "penaltyChargesCharged")
    val penaltyChargesCharged: Double? = null,

    @ColumnInfo(name = "penaltyChargesPaid")
    val penaltyChargesPaid: Double? = null,

    @ColumnInfo(name = "penaltyChargesWaived")
    val penaltyChargesWaived: Double? = null,

    @ColumnInfo(name = "penaltyChargesWrittenOff")
    val penaltyChargesWrittenOff: Double? = null,

    @ColumnInfo(name = "penaltyChargesOutstanding")
    val penaltyChargesOutstanding: Double? = null,

    @ColumnInfo(name = "penaltyChargesOverdue")
    val penaltyChargesOverdue: Double? = null,

    @ColumnInfo(name = "totalExpectedRepayment")
    val totalExpectedRepayment: Double? = null,

    @ColumnInfo(name = "totalRepayment")
    val totalRepayment: Double? = null,

    @ColumnInfo(name = "totalExpectedCostOfLoan")
    val totalExpectedCostOfLoan: Double? = null,

    @ColumnInfo(name = "totalCostOfLoan")
    val totalCostOfLoan: Double? = null,

    @ColumnInfo(name = "totalWaived")
    val totalWaived: Double? = null,

    @ColumnInfo(name = "totalWrittenOff")
    val totalWrittenOff: Double? = null,

    @ColumnInfo(name = "totalOutstanding")
    val totalOutstanding: Double? = null,

    @ColumnInfo(name = "totalOverdue")
    val totalOverdue: Double? = null,

    @ColumnInfo(name = "overdueSinceDate")
    val overdueSinceDate: List<Int>? = null,
) : Parcelable
