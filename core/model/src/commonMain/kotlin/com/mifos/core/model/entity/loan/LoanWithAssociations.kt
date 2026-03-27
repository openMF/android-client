/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.entity.loan

import com.mifos.core.model.objects.account.loan.AmortizationType
import com.mifos.core.model.objects.account.loan.InterestCalculationPeriodType
import com.mifos.core.model.objects.account.loan.InterestRateFrequencyType
import com.mifos.core.model.objects.account.loan.InterestType
import com.mifos.core.model.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.account.loan.TermPeriodFrequencyType
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LoanWithAssociations(
    val id: Int = 0,
    val accountNo: String = "",
    val status: LoanStatus = LoanStatus(),
    val clientId: Int = 0,
    val clientName: String = "",
    val clientOfficeId: Int = 0,
    val loanProductId: Int = 0,
    val loanProductName: String = "",
    val loanProductDescription: String = "",
    val fundId: Int = 0,
    val fundName: String = "",
    val loanPurposeId: Int = 0,
    val loanPurposeName: String = "",
    val loanOfficerId: Int = 0,
    val loanOfficerName: String = "",
    val loanType: LoanType = LoanType(),
    val currency: SavingAccountCurrency = SavingAccountCurrency(),
    val principal: Double = 0.0,
    val approvedPrincipal: Double = 0.0,
    val termFrequency: Int = 0,
    val termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),
    val numberOfRepayments: Int = 0,
    val repaymentEvery: Int = 0,
    val repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),
    val interestRatePerPeriod: Double = 0.0,
    val interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),
    val annualInterestRate: Double = 0.0,
    val amortizationType: AmortizationType = AmortizationType(),
    val interestType: InterestType = InterestType(),
    val interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),
    val transactionProcessingStrategyId: Int = 0,
    val transactionProcessingStrategyName: String = "",
    val syncDisbursementWithMeeting: Boolean = false,
    val timeline: LoanTimeline = LoanTimeline(),
    val summary: LoansAccountSummary = LoansAccountSummary(),
    val repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),
    val transactions: List<Transaction> = emptyList(),
    val feeChargesAtDisbursementCharged: Double = 0.0,
    val totalOverpaid: Double = 0.0,
    val loanCounter: Int = 0,
    val loanProductCounter: Int = 0,
    val multiDisburseLoan: Boolean = false,
    val canDisburse: Boolean = false,
    val inArrears: Boolean = false,
    val isNPA: Boolean = false,
) : Parcelable

@Parcelize
@Serializable
data class LoanStatus(
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
) : Parcelable

@Parcelize
@Serializable
data class LoanType(
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class SavingAccountCurrency(
    val id: Int = 0,
    val code: String? = null,
    val name: String? = null,
    val decimalPlaces: Int? = null,
    val inMultiplesOf: Int? = null,
    val displaySymbol: String? = null,
    val nameCode: String? = null,
    val displayLabel: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class LoanTimeline(
    val loanId: Int? = null,
    val submittedOnDate: List<Int>? = null,
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val approvedOnDate: List<Int>? = null,
    val approvedByUsername: String? = null,
    val approvedByFirstname: String? = null,
    val approvedByLastname: String? = null,
    val expectedDisbursementDate: List<Int>? = null,
    val actualDisburseDate: ActualDisbursementDate? = null,
    val actualDisbursementDate: List<Int?>? = null,
    val disbursedByUsername: String? = null,
    val disbursedByFirstname: String? = null,
    val disbursedByLastname: String? = null,
    val closedOnDate: List<Int>? = null,
    val expectedMaturityDate: List<Int>? = null,
) : Parcelable

@Parcelize
@Serializable
data class ActualDisbursementDate(
    val loanId: Int? = null,
    val year: Int? = null,
    val month: Int? = null,
    val date: Int? = null,
) : Parcelable

@Parcelize
@Serializable
data class LoansAccountSummary(
    val loanId: Int? = null,
    val currency: SavingAccountCurrency? = null,
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
