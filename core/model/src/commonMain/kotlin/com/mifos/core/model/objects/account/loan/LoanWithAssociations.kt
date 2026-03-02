/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class LoanWithAssociations(
    val id: Int? = null,
    val accountNo: String? = null,
    val externalId: String? = null,
    val status: LoanStatus? = LoanStatus.UNKNOWN,
    val clientId: Int? = null,
    val clientName: String? = null,
    val clientOfficeId: Int? = null,
    val loanOfficerName: String? = null,
    val loanProductId: Int? = null,
    val loanProductName: String? = null,
    val loanProductDescription: String? = null,
    val currency: Currency? = null,
    val principal: Double? = null,
    val approvedPrincipal: Double? = null,
    val termFrequency: Int? = null,
    val numberOfRepayments: Int? = null,
    val repaymentEvery: Int? = null,
    val interestRatePerPeriod: Double? = null,
    val annualInterestRate: Double? = null,
    val transactionProcessingStrategyId: Int? = null,
    val transactionProcessingStrategyName: String? = null,
    val syncDisbursementWithMeeting: Boolean? = null,
    val timeline: LoanTimeline? = null,
    val loanSummary: LoanSummary? = null,
    val feeChargesAtDisbursementCharged: Double? = null,
    val loanProductCounter: Int? = null,
    val multiDisburseLoan: Boolean? = null,
    val canDisburse: Boolean? = null,
    val inArrears: Boolean? = null,
    val npa: Boolean? = null,
    val repaymentFrequencyType: String? = null,
    val amortizationType: String? = null,
    val isEqualAmortization: Boolean? = null,
    val interestType: String? = null,
    val interestCalculationPeriodType: String? = null,
    val fundId: Int? = null,
    val fundName: String? = null,
    val loanPurposeId: Int? = null,
    val loanPurposeName: String? = null,
    val loanOfficerId: Int? = null,
    val chargedOff: Boolean? = null,
    val allowPartialPeriodInterestCalculation: Boolean? = null,
    val interestRecognitionOnDisbursementDate: Boolean? = null,
    val enableDownPayment: Boolean? = null,
    val enableIncomeCapitalization: Boolean? = null,
    val enableBuyDownFee: Boolean? = null,
    val enableInstallmentLevelDelinquency: Boolean? = null,
    val isInterestRecalculationEnabled: Boolean? = null,
    val chargeOffBehaviour: String? = null,
    val daysInYearType: String? = null,
    val daysInMonthType: String? = null,
    val availableDisbursementAmount: Double? = null,
    val totalOverpaid: Double? = null,
) : Parcelable
