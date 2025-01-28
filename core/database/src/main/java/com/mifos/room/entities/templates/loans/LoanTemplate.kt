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

import android.os.Parcelable
import com.mifos.core.entity.noncore.DataTable
import com.mifos.core.objects.account.loan.AccountLinkingOptions
import com.mifos.core.objects.template.loan.AmortizationType
import com.mifos.core.objects.template.loan.AmortizationTypeOptions
import com.mifos.core.objects.template.loan.ChargeOptions
import com.mifos.core.objects.template.loan.Currency
import com.mifos.core.objects.template.loan.DaysInMonthType
import com.mifos.core.objects.template.loan.DaysInYearType
import com.mifos.core.objects.template.loan.FundOptions
import com.mifos.core.objects.template.loan.InterestCalculationPeriodType
import com.mifos.core.objects.template.loan.InterestRateFrequencyType
import com.mifos.core.objects.template.loan.InterestRateFrequencyTypeOptions
import com.mifos.core.objects.template.loan.InterestType
import com.mifos.core.objects.template.loan.InterestTypeOptions
import com.mifos.core.objects.template.loan.LoanCollateralOptions
import com.mifos.core.objects.template.loan.LoanOfficerOptions
import com.mifos.core.objects.template.loan.LoanPurposeOptions
import com.mifos.core.objects.template.loan.Product
import com.mifos.core.objects.template.loan.ProductOptions
import com.mifos.core.objects.template.loan.RepaymentFrequencyDaysOfWeekTypeOptions
import com.mifos.core.objects.template.loan.RepaymentFrequencyNthDayTypeOptions
import com.mifos.core.objects.template.loan.RepaymentFrequencyType
import com.mifos.core.objects.template.loan.RepaymentFrequencyTypeOptions
import com.mifos.core.objects.template.loan.TermFrequencyTypeOptions
import com.mifos.core.objects.template.loan.TermPeriodFrequencyType
import com.mifos.core.objects.template.loan.Timeline
import com.mifos.core.objects.template.loan.TransactionProcessingStrategyOptions
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 15/07/16.
 */
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

    val allowPartialPeriodInterestCalcualtion: Boolean? = null,

    val transactionProcessingStrategyId: Int? = null,

    val graceOnArrearsAgeing: Int? = null,

    val timeline: Timeline? = null,

    val productOptions: List<ProductOptions> = ArrayList(),

    val dataTables: ArrayList<DataTable> = ArrayList(),

    val loanOfficerOptions: List<LoanOfficerOptions> = ArrayList(),

    val loanPurposeOptions: List<LoanPurposeOptions> = ArrayList(),

    val fundOptions: List<FundOptions> = ArrayList(),

    val termFrequencyTypeOptions: List<TermFrequencyTypeOptions> = ArrayList(),

    val repaymentFrequencyTypeOptions: List<RepaymentFrequencyTypeOptions> = ArrayList(),

    val repaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = ArrayList(),

    val repaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = ArrayList(),

    val interestRateFrequencyTypeOptions: List<InterestRateFrequencyTypeOptions> = ArrayList(),

    val amortizationTypeOptions: List<AmortizationTypeOptions> = ArrayList(),

    val interestTypeOptions: List<InterestTypeOptions> = ArrayList(),

    val interestCalculationPeriodTypeOptions: List<InterestCalculationPeriodType> = ArrayList(),

    val transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions> = ArrayList(),

    val chargeOptions: List<ChargeOptions> = ArrayList(),

    val loanCollateralOptions: List<LoanCollateralOptions> = ArrayList(),

    val multiDisburseLoan: Boolean? = null,

    val canDefineInstallmentAmount: Boolean? = null,

    val canDisburse: Boolean? = null,

    val product: Product? = null,

    val daysInMonthType: DaysInMonthType? = null,

    val daysInYearType: DaysInYearType? = null,

    val isInterestRecalculationEnabled: Boolean? = null,

    val isvaliableInstallmentsAllowed: Boolean? = null,

    val minimumGap: Int? = null,

    val maximumGap: Int? = null,

    val accountLinkingOptions: List<AccountLinkingOptions> = ArrayList(),
) : Parcelable
