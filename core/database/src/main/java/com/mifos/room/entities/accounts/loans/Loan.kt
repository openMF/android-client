/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import com.mifos.core.entity.accounts.loan.LoanType
import com.mifos.core.entity.accounts.loan.Status
import com.mifos.core.entity.accounts.loan.Summary
import com.mifos.core.entity.accounts.loan.Timeline
import com.mifos.core.model.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.objects.account.loan.AmortizationType
import com.mifos.core.objects.account.loan.Currency
import com.mifos.core.objects.account.loan.InterestCalculationPeriodType
import com.mifos.core.objects.account.loan.InterestRateFrequencyType
import com.mifos.core.objects.account.loan.InterestType
import com.mifos.core.objects.account.loan.TermPeriodFrequencyType

data class Loan(
    val id: Int? = null,

    val accountNo: String? = null,

    val status: Status? = null,

    val clientId: Int? = null,

    val clientName: String? = null,

    val clientOfficeId: Int? = null,

    val loanProductId: Int? = null,

    val loanProductName: String? = null,

    val loanProductDescription: String? = null,

    val fundId: Int? = null,

    val fundName: String? = null,

    val loanOfficerId: Int? = null,

    val loanOfficerName: String? = null,

    val loanType: LoanType? = null,

    val currency: Currency? = null,

    val principal: Double? = null,

    val approvedPrincipal: Double? = null,

    val termFrequency: Int? = null,

    val termPeriodFrequencyType: TermPeriodFrequencyType? = null,

    val numberOfRepayments: Int? = null,

    val repaymentEvery: Int? = null,

    val repaymentFrequencyType: RepaymentFrequencyType? = null,

    val interestRatePerPeriod: Double? = null,

    val interestRateFrequencyType: InterestRateFrequencyType? = null,

    val annualInterestRate: Double? = null,

    val amortizationType: AmortizationType? = null,

    val interestType: InterestType? = null,

    val interestCalculationPeriodType: InterestCalculationPeriodType? = null,

    val transactionProcessingStrategyId: Int? = null,

    val transactionProcessingStrategyName: String? = null,

    val syncDisbursementWithMeeting: Boolean? = null,

    val timeline: Timeline? = null,

    val summary: Summary? = null,

    val feeChargesAtDisbursementCharged: Double? = null,

    val loanCounter: Int? = null,

    val loanProductCounter: Int? = null,

    val multiDisburseLoan: Boolean? = null,

    val canDisburse: Boolean? = null,

    val inArrears: Boolean? = null,

    val isNPA: Boolean? = null,

    val overdueCharges: List<Any> = emptyList(),

    private val additionalProperties: MutableMap<String, Any> = mutableMapOf(),
)
