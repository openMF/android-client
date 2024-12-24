/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * This Model is saving the Summary of the Loans according to their Id's
 *
 */
@Parcelize
@Entity("Summary")
data class Summary(
    @PrimaryKey
    @Transient
    var loanId: Int? = null,

    var currency: Currency? = null,

    @ColumnInfo("principalDisbursed")
    var principalDisbursed: Double? = null,

    @ColumnInfo("principalPaid")
    var principalPaid: Double? = null,

    var principalWrittenOff: Double? = null,

    @ColumnInfo("principalOutstanding")
    var principalOutstanding: Double? = null,

    var principalOverdue: Double? = null,

    @ColumnInfo("interestCharged")
    var interestCharged: Double? = null,

    @ColumnInfo("interestPaid")
    var interestPaid: Double? = null,

    var interestWaived: Double? = null,

    var interestWrittenOff: Double? = null,

    @ColumnInfo("interestOutstanding")
    var interestOutstanding: Double? = null,

    var interestOverdue: Double? = null,

    @ColumnInfo("feeChargesCharged")
    var feeChargesCharged: Double? = null,

    var feeChargesDueAtDisbursementCharged: Double? = null,

    @ColumnInfo("feeChargesPaid")
    var feeChargesPaid: Double? = null,

    var feeChargesWaived: Double? = null,

    var feeChargesWrittenOff: Double? = null,

    @ColumnInfo("feeChargesOutstanding")
    var feeChargesOutstanding: Double? = null,

    var feeChargesOverdue: Double? = null,

    @ColumnInfo("penaltyChargesCharged")
    var penaltyChargesCharged: Double? = null,

    @ColumnInfo("penaltyChargesPaid")
    var penaltyChargesPaid: Double? = null,

    var penaltyChargesWaived: Double? = null,

    var penaltyChargesWrittenOff: Double? = null,

    @ColumnInfo("penaltyChargesOutstanding")
    var penaltyChargesOutstanding: Double? = null,

    var penaltyChargesOverdue: Double? = null,

    @ColumnInfo("totalExpectedRepayment")
    var totalExpectedRepayment: Double? = null,

    @ColumnInfo("totalRepayment")
    var totalRepayment: Double? = null,

    var totalExpectedCostOfLoan: Double? = null,

    var totalCostOfLoan: Double? = null,

    var totalWaived: Double? = null,

    var totalWrittenOff: Double? = null,

    @ColumnInfo("totalOutstanding")
    var totalOutstanding: Double? = null,

    @ColumnInfo("totalOverdue")
    var totalOverdue: Double? = null,

    var overdueSinceDate: List<Int>? = null,
) : MifosBaseModel(), Parcelable
