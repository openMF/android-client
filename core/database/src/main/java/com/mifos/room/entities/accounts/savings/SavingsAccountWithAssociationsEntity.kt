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
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.core.model.objects.account.saving.InterestCalculationDaysInYearType
import com.mifos.core.model.objects.account.saving.InterestCalculationType
import com.mifos.core.model.objects.account.saving.InterestCompoundingPeriodType
import com.mifos.core.model.objects.account.saving.InterestPostingPeriodType
import com.mifos.core.model.objects.account.saving.LockinPeriodFrequencyType
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import kotlinx.serialization.Serializable

@Entity(
    tableName = "SavingsAccountWithAssociations",
    foreignKeys = [
        ForeignKey(
            entity = SavingsAccountStatusEntity::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SavingsAccountSummaryEntity::class,
            parentColumns = ["savingsId"],
            childColumns = ["summary"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
@Serializable
data class SavingsAccountWithAssociationsEntity(
    @PrimaryKey
    val id: Int? = null,

    val accountNo: Int? = null,

    val clientId: Int? = null,

    val clientName: String? = null,

    val savingsProductId: Int? = null,

    val savingsProductName: String? = null,

    val fieldOfficerId: Int? = null,

    @ColumnInfo(index = true)
    val status: SavingsAccountStatusEntity? = null,

    val timeline: LoanTimelineEntity? = null,

    val currency: SavingAccountCurrencyEntity? = null,

    val nominalAnnualInterestRate: Double? = null,

    val interestCompoundingPeriodType: InterestCompoundingPeriodType? = null,

    val interestPostingPeriodType: InterestPostingPeriodType? = null,

    val interestCalculationType: InterestCalculationType? = null,

    val interestCalculationDaysInYearType: InterestCalculationDaysInYearType? = null,

    val minRequiredOpeningBalance: Double? = null,

    val lockinPeriodFrequency: Int? = null,

    val lockinPeriodFrequencyType: LockinPeriodFrequencyType? = null,

    val withdrawalFeeForTransfers: Boolean? = null,

    val allowOverdraft: Boolean? = null,

    val enforceMinRequiredBalance: Boolean? = null,

    val withHoldTax: Boolean? = null,

    val lastActiveTransactionDate: List<Int?> = emptyList(),

    val dormancyTrackingActive: Boolean? = null,

    val overdraftLimit: Int? = null,

    @ColumnInfo(index = true)
    val summary: SavingsAccountSummaryEntity? = null,

    val transactions: List<SavingsAccountTransactionEntity> = emptyList(),

    val charges: List<Charge?> = emptyList(),
)
