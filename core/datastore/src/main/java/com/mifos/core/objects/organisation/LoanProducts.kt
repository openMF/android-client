/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.organisation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 *
 * Created by Rajan Maurya on 12/15/2015.
 */
@Parcelize
data class LoanProducts(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("shortName")
    var shortName: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("fundId")
    var fundId: Int? = null,

    @SerializedName("fundName")
    var fundName: String? = null,

    @SerializedName("includeInBorrowerCycle")
    var includeInBorrowerCycle: Boolean? = null,

    @SerializedName("useBorrowerCycle")
    var useBorrowerCycle: Boolean? = null,

    @SerializedName("startDate")
    var startDate: List<Int?>? = null,

    @SerializedName("closeDate")
    var closeDate: List<Int?>? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("currency")
    var currency: Currency? = null,

    @SerializedName("principal")
    var principal: Double? = null,

    @SerializedName("minPrincipal")
    var minPrincipal: Double? = null,

    @SerializedName("maxPrincipal")
    var maxPrincipal: Double? = null,

    @SerializedName("numberOfRepayments")
    var numberOfRepayments: Int? = null,

    @SerializedName("minNumberOfRepayments")
    var minNumberOfRepayments: Int? = null,

    @SerializedName("maxNumberOfRepayments")
    var maxNumberOfRepayments: Int? = null,

    @SerializedName("repaymentEvery")
    var repaymentEvery: Int? = null,

    @SerializedName("repaymentFrequencyType")
    var repaymentFrequencyType: RepaymentFrequencyType? = null,

    @SerializedName("interestRatePerPeriod")
    var interestRatePerPeriod: Double? = null,

    @SerializedName("minInterestRatePerPeriod")
    var minInterestRatePerPeriod: Double? = null,

    @SerializedName("maxInterestRatePerPeriod")
    var maxInterestRatePerPeriod: Double? = null,

    @SerializedName("interestRateFrequencyType")
    var interestRateFrequencyType: InterestRateFrequencyType? = null,

    @SerializedName("annualInterestRate")
    var annualInterestRate: Double? = null,

    @SerializedName("isLinkedToFloatingInterestRates")
    var linkedToFloatingInterestRates: Boolean? = null,

    @SerializedName("isFloatingInterestRateCalculationAllowed")
    var floatingInterestRateCalculationAllowed: Boolean? = null,

    @SerializedName("allowVariableInstallments")
    var allowVariableInstallments: Boolean? = null,

    @SerializedName("minimumGap")
    var minimumGap: Double? = null,

    @SerializedName("maximumGap")
    var maximumGap: Double? = null,

    @SerializedName("amortizationType")
    var amortizationType: AmortizationType? = null,

    @SerializedName("interestType")
    var interestType: InterestType? = null,

    @SerializedName("interestCalculationPeriodType")
    var interestCalculationPeriodType: InterestCalculationPeriodType? = null,

    @SerializedName("allowPartialPeriodInterestCalcualtion")
    var allowPartialPeriodInterestCalcualtion: Boolean? = null,

    @SerializedName("transactionProcessingStrategyId")
    var transactionProcessingStrategyId: Int? = null,

    @SerializedName("transactionProcessingStrategyName")
    var transactionProcessingStrategyName: String? = null,

    @SerializedName("graceOnPrincipalPayment")
    var graceOnPrincipalPayment: Int? = null,

    @SerializedName("graceOnInterestPayment")
    var graceOnInterestPayment: Int? = null,

    @SerializedName("daysInMonthType")
    var daysInMonthType: DaysInMonthType? = null,

    @SerializedName("daysInYearType")
    var daysInYearType: DaysInYearType? = null,

    @SerializedName("isInterestRecalculationEnabled")
    var interestRecalculationEnabled: Boolean? = null,

    @SerializedName("canDefineInstallmentAmount")
    var canDefineInstallmentAmount: Boolean? = null,

    @SerializedName("installmentAmountInMultiplesOf")
    var installmentAmountInMultiplesOf: Int? = null,

    @SerializedName("accountingRule")
    var accountingRule: AccountingRule? = null,

    @SerializedName("multiDisburseLoan")
    var multiDisburseLoan: Boolean? = null,

    @SerializedName("maxTrancheCount")
    var maxTrancheCount: Int? = null,

    @SerializedName("principalThresholdForLastInstallment")
    var principalThresholdForLastInstallment: Int? = null,

    @SerializedName("holdGuaranteeFunds")
    var holdGuaranteeFunds: Boolean? = null,

    @SerializedName("accountMovesOutOfNPAOnlyOnArrearsCompletion")
    var accountMovesOutOfNPAOnlyOnArrearsCompletion: Boolean? = null,

    @SerializedName("allowAttributeOverrides")
    var allowAttributeOverrides: AllowAttributeOverrides? = null
) : Parcelable