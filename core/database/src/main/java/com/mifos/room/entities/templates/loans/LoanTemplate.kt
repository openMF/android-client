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
    var clientId: Int? = null,

    var clientAccountNo: String? = null,

    var clientName: String? = null,

    var clientOfficeId: Int? = null,

    var loanProductId: Int? = null,

    var loanProductName: String? = null,

    var isLoanProductLinkedToFloatingRate: Boolean? = null,

    var fundId: Int? = null,

    var fundName: String? = null,

    var currency: Currency? = null,

    var principal: Double? = null,

    var approvedPrincipal: Double? = null,

    var proposedPrincipal: Double? = null,

    var termFrequency: Int? = null,

    var termPeriodFrequencyType: TermPeriodFrequencyType? = null,

    var numberOfRepayments: Int? = null,

    var repaymentEvery: Int? = null,

    var repaymentFrequencyType: RepaymentFrequencyType? = null,

    var interestRatePerPeriod: Double? = null,

    var interestRateFrequencyType: InterestRateFrequencyType? = null,

    var annualInterestRate: Double? = null,

    var isFloatingInterestRate: Boolean? = null,

    var amortizationType: AmortizationType? = null,

    var interestType: InterestType? = null,

    var interestCalculationPeriodType: InterestCalculationPeriodType? = null,

    var allowPartialPeriodInterestCalcualtion: Boolean? = null,

    var transactionProcessingStrategyId: Int? = null,

    var graceOnArrearsAgeing: Int? = null,

    var timeline: Timeline? = null,

    var productOptions: List<ProductOptions> = ArrayList(),

    var dataTables: ArrayList<DataTable> = ArrayList(),

    var loanOfficerOptions: List<LoanOfficerOptions> = ArrayList(),

    var loanPurposeOptions: List<LoanPurposeOptions> = ArrayList(),

    var fundOptions: List<FundOptions> = ArrayList(),

    var termFrequencyTypeOptions: List<TermFrequencyTypeOptions> = ArrayList(),

    var repaymentFrequencyTypeOptions: List<RepaymentFrequencyTypeOptions> = ArrayList(),

    var repaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = ArrayList(),

    var repaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = ArrayList(),

    var interestRateFrequencyTypeOptions: List<InterestRateFrequencyTypeOptions> = ArrayList(),

    var amortizationTypeOptions: List<AmortizationTypeOptions> = ArrayList(),

    var interestTypeOptions: List<InterestTypeOptions> = ArrayList(),

    var interestCalculationPeriodTypeOptions: List<InterestCalculationPeriodType> = ArrayList(),

    var transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions> = ArrayList(),

    var chargeOptions: List<ChargeOptions> = ArrayList(),

    var loanCollateralOptions: List<LoanCollateralOptions> = ArrayList(),

    var multiDisburseLoan: Boolean? = null,

    var canDefineInstallmentAmount: Boolean? = null,

    var canDisburse: Boolean? = null,

    var product: Product? = null,

    var daysInMonthType: DaysInMonthType? = null,

    var daysInYearType: DaysInYearType? = null,

    var isInterestRecalculationEnabled: Boolean? = null,

    var isVariableInstallmentsAllowed: Boolean? = null,

    var minimumGap: Int? = null,

    var maximumGap: Int? = null,

    var accountLinkingOptions: List<AccountLinkingOptions> = ArrayList(),
) : Parcelable
