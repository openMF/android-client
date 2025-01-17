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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.core.objects.account.saving.InterestCalculationDaysInYearType
import com.mifos.core.objects.account.saving.InterestCalculationType
import com.mifos.core.objects.account.saving.InterestCompoundingPeriodType
import com.mifos.core.objects.account.saving.InterestPostingPeriodType
import com.mifos.core.objects.account.saving.LockinPeriodFrequencyType
import com.mifos.core.objects.account.saving.Timeline

@Entity(
    tableName = "SavingsAccountWithAssociations",
    foreignKeys = [
        ForeignKey(
            entity = Status::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Summary::class,
            parentColumns = ["loanId"],
            childColumns = ["summary"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SavingsAccountWithAssociations(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "accountNo")
    var accountNo: Int? = null,

    @ColumnInfo(name = "clientId")
    var clientId: Int? = null,

    @ColumnInfo(name = "clientName")
    var clientName: String? = null,

    @ColumnInfo(name = "savingsProductId")
    var savingsProductId: Int? = null,

    @ColumnInfo(name = "savingsProductName")
    var savingsProductName: String? = null,

    @ColumnInfo(name = "fieldOfficerId")
    var fieldOfficerId: Int? = null,

    @ColumnInfo(name = "status", index = true)
    var status: Status? = null,

    @Embedded
    var timeline: Timeline? = null,

    @Embedded
    var currency: Currency? = null,

    @ColumnInfo(name = "nominalAnnualInterestRate")
    var nominalAnnualInterestRate: Double? = null,

    @ColumnInfo(name = "interestCompoundingPeriodType")
    var interestCompoundingPeriodType: InterestCompoundingPeriodType? = null,

    @ColumnInfo(name = "interestPostingPeriodType")
    var interestPostingPeriodType: InterestPostingPeriodType? = null,

    @ColumnInfo(name = "interestCalculationType")
    var interestCalculationType: InterestCalculationType? = null,

    @ColumnInfo(name = "interestCalculationDaysInYearType")
    var interestCalculationDaysInYearType: InterestCalculationDaysInYearType? = null,

    @ColumnInfo(name = "minRequiredOpeningBalance")
    var minRequiredOpeningBalance: Double? = null,

    @ColumnInfo(name = "lockinPeriodFrequency")
    var lockinPeriodFrequency: Int? = null,

    @ColumnInfo(name = "lockinPeriodFrequencyType")
    var lockinPeriodFrequencyType: LockinPeriodFrequencyType? = null,

    @ColumnInfo(name = "withdrawalFeeForTransfers")
    var withdrawalFeeForTransfers: Boolean? = null,

    @ColumnInfo(name = "allowOverdraft")
    var allowOverdraft: Boolean? = null,

    @ColumnInfo(name = "enforceMinRequiredBalance")
    var enforceMinRequiredBalance: Boolean? = null,

    @ColumnInfo(name = "withHoldTax")
    var withHoldTax: Boolean? = null,

    @ColumnInfo(name = "lastActiveTransactionDate")
    var lastActiveTransactionDate: List<Int?> = ArrayList(),

    @ColumnInfo(name = "dormancyTrackingActive")
    var dormancyTrackingActive: Boolean? = null,

    @ColumnInfo(name = "overdraftLimit")
    var overdraftLimit: Int? = null,

    @ColumnInfo(name = "summary", index = true)
    var summary: Summary? = null,

    @ColumnInfo(name = "transactions")
    var transactions: List<Transaction> = ArrayList(),

    @ColumnInfo(name = "charges")
    var charges: List<Charge?> = ArrayList(),
)
