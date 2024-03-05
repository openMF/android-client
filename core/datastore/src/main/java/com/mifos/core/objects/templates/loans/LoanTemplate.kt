package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.AccountLinkingOptions
import com.mifos.core.objects.noncore.DataTable
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

    var accountLinkingOptions: List<AccountLinkingOptions> = ArrayList()
) : Parcelable