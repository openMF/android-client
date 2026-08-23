/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.loans

import com.mifos.core.model.objects.account.loan.AccountLinkingOptions
import com.mifos.core.model.objects.template.loan.AmortizationType
import com.mifos.core.model.objects.template.loan.AmortizationTypeOptions
import com.mifos.core.model.objects.template.loan.ChargeOptions
import com.mifos.core.model.objects.template.loan.Currency
import com.mifos.core.model.objects.template.loan.DaysInMonthType
import com.mifos.core.model.objects.template.loan.DaysInYearType
import com.mifos.core.model.objects.template.loan.FundOptions
import com.mifos.core.model.objects.template.loan.InterestCalculationPeriodType
import com.mifos.core.model.objects.template.loan.InterestRateFrequencyType
import com.mifos.core.model.objects.template.loan.InterestRateFrequencyTypeOptions
import com.mifos.core.model.objects.template.loan.InterestType
import com.mifos.core.model.objects.template.loan.InterestTypeOptions
import com.mifos.core.model.objects.template.loan.LoanCollateralOptions
import com.mifos.core.model.objects.template.loan.LoanOfficerOption
import com.mifos.core.model.objects.template.loan.LoanPurposeOptions
import com.mifos.core.model.objects.template.loan.Product
import com.mifos.core.model.objects.template.loan.ProductOptions
import com.mifos.core.model.objects.template.loan.RepaymentFrequencyDaysOfWeekTypeOptions
import com.mifos.core.model.objects.template.loan.RepaymentFrequencyNthDayTypeOptions
import com.mifos.core.model.objects.template.loan.RepaymentFrequencyType
import com.mifos.core.model.objects.template.loan.RepaymentFrequencyTypeOptions
import com.mifos.core.model.objects.template.loan.TermFrequencyTypeOptions
import com.mifos.core.model.objects.template.loan.TermPeriodFrequencyType
import com.mifos.core.model.objects.template.loan.Timeline
import com.mifos.core.model.objects.template.loan.TransactionProcessingStrategyOptions
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Serializable
data class LoanTemplate(
    val clientId: Int? = null,

    val clientAccountNo: String? = null,

    val clientName: String? = null,

    val clientOfficeId: Int? = null,

    val loanProductId: Int? = null,

    val loanProductName: String? = null,

    val isLoanProductLinkedToFloatingRate: Boolean? = null,

    val fundId: Int? = null,

    val fundName: String? = null,

    val currency: Currency? = null,

    val principal: Double? = null,

    val approvedPrincipal: Double? = null,

    val proposedPrincipal: Double? = null,

    val termFrequency: Int? = null,

    val termPeriodFrequencyType: TermPeriodFrequencyType? = null,

    val numberOfRepayments: Int? = null,

    val repaymentEvery: Int? = null,

    val repaymentFrequencyType: RepaymentFrequencyType? = null,

    val interestRatePerPeriod: Double? = null,

    val interestRateFrequencyType: InterestRateFrequencyType? = null,

    val annualInterestRate: Double? = null,

    val isFloatingInterestRate: Boolean? = null,

    val amortizationType: AmortizationType? = null,

    val interestType: InterestType? = null,

    val interestCalculationPeriodType: InterestCalculationPeriodType? = null,

    val allowPartialPeriodInterestCalculation: Boolean? = null,

    val transactionProcessingStrategyId: Int? = null,

    val graceOnArrearsAgeing: Int? = null,

    val timeline: Timeline? = null,

    val productOptions: List<ProductOptions> = emptyList(),

    val dataTables: ArrayList<DataTableEntity> = ArrayList(),

    val loanOfficerOptions: List<LoanOfficerOption> = emptyList(),

    val loanPurposeOptions: List<LoanPurposeOptions> = emptyList(),

    val fundOptions: List<FundOptions> = emptyList(),

    val termFrequencyTypeOptions: List<TermFrequencyTypeOptions> = emptyList(),

    val repaymentFrequencyTypeOptions: List<RepaymentFrequencyTypeOptions> = emptyList(),

    val repaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = emptyList(),

    val repaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = emptyList(),

    val interestRateFrequencyTypeOptions: List<InterestRateFrequencyTypeOptions> = emptyList(),

    val amortizationTypeOptions: List<AmortizationTypeOptions> = emptyList(),

    val interestTypeOptions: List<InterestTypeOptions> = emptyList(),

    val interestCalculationPeriodTypeOptions: List<InterestCalculationPeriodType> = emptyList(),

    val transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions> = emptyList(),

    val chargeOptions: List<ChargeOptions> = emptyList(),

    val loanCollateralOptions: List<LoanCollateralOptions> = emptyList(),

    val multiDisburseLoan: Boolean? = null,

    val transactionProcessingStrategyCode: String? = null,

    val canDefineInstallmentAmount: Boolean? = null,

    val canDisburse: Boolean? = null,

    val product: Product? = null,

    val daysInMonthType: DaysInMonthType? = null,

    val daysInYearType: DaysInYearType? = null,

    val isInterestRecalculationEnabled: Boolean? = null,

    val isvaliableInstallmentsAllowed: Boolean? = null,

    val minimumGap: Int? = null,

    val maximumGap: Int? = null,

    val accountLinkingOptions: List<AccountLinkingOptions> = emptyList(),

    val loanScheduleType: TermFrequencyTypeOptions? = null,

    val overdueCharges: List<ChargesEntity> = emptyList(),

)
