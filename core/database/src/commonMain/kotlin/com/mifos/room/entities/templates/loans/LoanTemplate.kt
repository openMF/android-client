/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
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
import com.mifos.core.model.objects.template.loan.LoanOfficerOptions
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
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Serializable
@Parcelize
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

    @IgnoredOnParcel
    val currency: Currency? = null,

    val principal: Double? = null,

    val approvedPrincipal: Double? = null,

    val proposedPrincipal: Double? = null,

    val termFrequency: Int? = null,

    @IgnoredOnParcel
    val termPeriodFrequencyType: TermPeriodFrequencyType? = null,

    val numberOfRepayments: Int? = null,

    val repaymentEvery: Int? = null,

    @IgnoredOnParcel
    val repaymentFrequencyType: RepaymentFrequencyType? = null,

    val interestRatePerPeriod: Double? = null,

    @IgnoredOnParcel
    val interestRateFrequencyType: InterestRateFrequencyType? = null,

    val annualInterestRate: Double? = null,

    val isFloatingInterestRate: Boolean? = null,

    @IgnoredOnParcel
    val amortizationType: AmortizationType? = null,

    @IgnoredOnParcel
    val interestType: InterestType? = null,

    @IgnoredOnParcel
    val interestCalculationPeriodType: InterestCalculationPeriodType? = null,

    val allowPartialPeriodInterestCalcualtion: Boolean? = null,

    val transactionProcessingStrategyId: Int? = null,

    val graceOnArrearsAgeing: Int? = null,

    @IgnoredOnParcel
    val timeline: Timeline? = null,

    @IgnoredOnParcel
    val productOptions: List<ProductOptions> = emptyList(),

    val dataTables: ArrayList<DataTableEntity> = ArrayList(),

    @IgnoredOnParcel
    val loanOfficerOptions: List<LoanOfficerOptions> = emptyList(),

    @IgnoredOnParcel
    val loanPurposeOptions: List<LoanPurposeOptions> = emptyList(),

    @IgnoredOnParcel
    val fundOptions: List<FundOptions> = emptyList(),

    @IgnoredOnParcel
    val termFrequencyTypeOptions: List<TermFrequencyTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val repaymentFrequencyTypeOptions: List<RepaymentFrequencyTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val repaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val repaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val interestRateFrequencyTypeOptions: List<InterestRateFrequencyTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val amortizationTypeOptions: List<AmortizationTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val interestTypeOptions: List<InterestTypeOptions> = emptyList(),

    @IgnoredOnParcel
    val interestCalculationPeriodTypeOptions: List<InterestCalculationPeriodType> = emptyList(),

    @IgnoredOnParcel
    val transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions> = emptyList(),

    @IgnoredOnParcel
    val chargeOptions: List<ChargeOptions> = emptyList(),

    @IgnoredOnParcel
    val loanCollateralOptions: List<LoanCollateralOptions> = emptyList(),

    val multiDisburseLoan: Boolean? = null,

    val canDefineInstallmentAmount: Boolean? = null,

    val canDisburse: Boolean? = null,

    @IgnoredOnParcel
    val product: Product? = null,

    @IgnoredOnParcel
    val daysInMonthType: DaysInMonthType? = null,

    @IgnoredOnParcel
    val daysInYearType: DaysInYearType? = null,

    val isInterestRecalculationEnabled: Boolean? = null,

    val isvaliableInstallmentsAllowed: Boolean? = null,

    val minimumGap: Int? = null,

    val maximumGap: Int? = null,

    @IgnoredOnParcel
    val accountLinkingOptions: List<AccountLinkingOptions> = emptyList(),
) : Parcelable
