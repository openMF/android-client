package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class Product(
    var id: Int = 0,
    var name: String = "",
    var shortName: String = "",
    var fundId: Int = 0,
    var fundName: String = "",
    var includeInBorrowerCycle: Boolean = false,
    var useBorrowerCycle: Boolean = false,
    var startDate: List<Int> = ArrayList(),
    var status: String = "",
    var currency: Currency = Currency(),
    var principal: Double = 0.0,
    var minPrincipal: Double = 0.0,
    var maxPrincipal: Double = 0.0,
    var numberOfRepayments: Int = 0,
    var minNumberOfRepayments: Int = 0,
    var maxNumberOfRepayments: Int = 0,
    var repaymentEvery: Int = 0,
    var repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),
    var interestRatePerPeriod: Double = 0.0,
    var minInterestRatePerPeriod: Double = 0.0,
    var maxInterestRatePerPeriod: Double = 0.0,
    var interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),
    var annualInterestRate: Double = 0.0,
    var isLinkedToFloatingInterestRates: Boolean = false,
    var isFloatingInterestRateCalculationAllowed: Boolean = false,
    var allowVariableInstallments: Boolean = false,
    var minimumGap: Double = 0.0,
    var maximumGap: Double = 0.0,
    var amortizationType: AmortizationType = AmortizationType(),
    var interestType: InterestType = InterestType(),
    var interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),
    var allowPartialPeriodInterestCalcualtion: Boolean = false,
    var transactionProcessingStrategyId: Int = 0,
    var transactionProcessingStrategyName: String = "",
    var graceOnArrearsAgeing: Int = 0,
    var overdueDaysForNPA: Int = 0,
    var daysInMonthType: DaysInMonthType = DaysInMonthType(),
    var daysInYearType: DaysInYearType = DaysInYearType(),
    var isInterestRecalculationEnabled: Boolean = false,
    var interestRecalculationData: InterestRecalculationData = InterestRecalculationData(),
    var canDefineInstallmentAmount: Boolean = false,
    var accountingRule: AccountingRule = AccountingRule(),
    var multiDisburseLoan: Boolean = false,
    var maxTrancheCount: Int = 0,
    var principalThresholdForLastInstallment: Int = 0,
    var holdGuaranteeFunds: Boolean = false,
    var accountMovesOutOfNPAOnlyOnArrearsCompletion: Boolean = false,
    var allowAttributeOverrides: AllowAttributeOverrides = AllowAttributeOverrides()
) : Parcelable