/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.recurring

import com.mifos.room.entities.accounts.recurring.deposit.DepositPeriodFrequency
import com.mifos.room.entities.accounts.recurring.deposit.DepositType
import com.mifos.room.entities.accounts.recurring.deposit.InMultiplesOfDepositTermType
import com.mifos.room.entities.accounts.recurring.deposit.MaxDepositTermType
import com.mifos.room.entities.accounts.recurring.deposit.MinDepositTermType
import com.mifos.room.entities.accounts.recurring.interest.InterestCalculationDaysInYearType
import com.mifos.room.entities.accounts.recurring.interest.InterestCalculationType
import com.mifos.room.entities.accounts.recurring.interest.InterestCompoundingPeriodType
import com.mifos.room.entities.accounts.recurring.interest.InterestPostingPeriodType
import kotlinx.serialization.Serializable

@Serializable
data class RecurringDeposit(
    val accountChart: AccountChart? = null,
    val accountNo: String? = null,
    val adjustAdvanceTowardsFuturePayments: Boolean? = null,
    val allowWithdrawal: Boolean? = null,
    val clientId: Int? = null,
    val clientName: String? = null,
    val currency: Currency? = null,
    val depositAmount: Double? = null,
    val depositPeriod: Int? = null,
    val depositPeriodFrequency: DepositPeriodFrequency? = null,
    val depositProductId: Int? = null,
    val depositProductName: String? = null,
    val depositType: DepositType? = null,
    val expectedFirstDepositOnDate: List<Int>? = null,
    val externalId: String? = null,
    val fieldOfficerId: Int? = null,
    val fieldOfficerName: String? = null,
    val id: Int? = null,
    val inMultiplesOfDepositTermType: InMultiplesOfDepositTermType? = null,
    val interestCalculationDaysInYearType: InterestCalculationDaysInYearType? = null,
    val interestCalculationType: InterestCalculationType? = null,
    val interestCompoundingPeriodType: InterestCompoundingPeriodType? = null,
    val interestPostingPeriodType: InterestPostingPeriodType? = null,
    val isCalendarInherited: Boolean? = null,
    val isMandatoryDeposit: Boolean? = null,
    val lockinPeriodFrequency: Int? = null,
    val lockinPeriodFrequencyType: LockinPeriodFrequencyType? = null,
    val mandatoryRecommendedDepositAmount: Double? = null,
    val maturityAmount: Double? = null,
    val maturityDate: List<Int>? = null,
    val maxDepositTermType: MaxDepositTermType? = null,
    val minDepositTerm: Int? = null,
    val minDepositTermType: MinDepositTermType? = null,
    val nominalAnnualInterestRate: Double? = null,
    val preClosurePenalApplicable: Boolean? = null,
    val recurringFrequency: Int? = null,
    val recurringFrequencyType: RecurringFrequencyType? = null,
    val status: Status? = null,
    val summary: Summary? = null,
    val timeline: Timeline? = null,
    val withHoldTax: Boolean? = null,
    val withdrawalFeeForTransfers: Boolean? = null,
)
