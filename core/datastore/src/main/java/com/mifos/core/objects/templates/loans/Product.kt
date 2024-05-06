package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class Product(
    var id: Int? = null,

    var name: String? = null,

    var shortName: String? = null,

    var fundId: Int? = null,

    var fundName: String? = null,

    var includeInBorrowerCycle: Boolean? = null,

    var useBorrowerCycle: Boolean? = null,

    var startDate: List<Int>? = null,

    var status: String? = null,

    var currency: Currency? = null,

    var principal: Double? = null,

    var minPrincipal: Double? = null,

    var maxPrincipal: Double? = null,

    var numberOfRepayments: Int? = null,

    var minNumberOfRepayments: Int? = null,

    var maxNumberOfRepayments: Int? = null,

    var repaymentEvery: Int? = null,

    var repaymentFrequencyType: RepaymentFrequencyType? = null,

    var interestRatePerPeriod: Double? = null,

    var minInterestRatePerPeriod: Double? = null,

    var maxInterestRatePerPeriod: Double? = null,

    var interestRateFrequencyType: InterestRateFrequencyType? = null,

    var annualInterestRate: Double? = null,

    var isLinkedToFloatingInterestRates: Boolean? = null,

    var isFloatingInterestRateCalculationAllowed: Boolean? = null,

    var allowVariableInstallments: Boolean? = null,

    var minimumGap: Double? = null,

    var maximumGap: Double? = null,

    var amortizationType: AmortizationType? = null,

    var interestType: InterestType? = null,

    var interestCalculationPeriodType: InterestCalculationPeriodType? = null,

    var allowPartialPeriodInterestCalcualtion: Boolean? = null,

    var transactionProcessingStrategyId: Int? = null,

    var transactionProcessingStrategyName: String? = null,

    var graceOnArrearsAgeing: Int? = null,

    var overdueDaysForNPA: Int? = null,

    var daysInMonthType: DaysInMonthType? = null,

    var daysInYearType: DaysInYearType? = null,

    var isInterestRecalculationEnabled: Boolean? = null,

    var interestRecalculationData: InterestRecalculationData? = null,

    var canDefineInstallmentAmount: Boolean? = null,

    var accountingRule: AccountingRule? = null,

    var multiDisburseLoan: Boolean? = null,

    var maxTrancheCount: Int? = null,

    var principalThresholdForLastInstallment: Int? = null,

    var holdGuaranteeFunds: Boolean? = null,

    var accountMovesOutOfNPAOnlyOnArrearsCompletion: Boolean? = null,

    var allowAttributeOverrides: AllowAttributeOverrides? = null
) : Parcelable