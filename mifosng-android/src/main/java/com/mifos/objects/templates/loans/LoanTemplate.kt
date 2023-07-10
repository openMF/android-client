package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.objects.accounts.loan.AccountLinkingOptions
import com.mifos.objects.noncore.DataTable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Parcelize
data class LoanTemplate(
    @SerializedName("clientId")
    var clientId: Int = 0,
    @SerializedName("clientAccountNo")
    var clientAccountNo: String = "",
    @SerializedName("clientName")
    var clientName: String = "",
    @SerializedName("clientOfficeId")
    var clientOfficeId: Int = 0,
    @SerializedName("loanProductId")
    var loanProductId: Int = 0,
    @SerializedName("loanProductName")
    var loanProductName: String = "",
    @SerializedName("isLoanProductLinkedToFloatingRate")
    var isLoanProductLinkedToFloatingRate: Boolean = false,
    @SerializedName("fundId")
    var fundId: Int = 0,
    @SerializedName("fundName")
    var fundName: String = "",
    @SerializedName("currency")
    var currency: Currency = Currency(),
    @SerializedName("principal")
    var principal: Double = 0.0,
    @SerializedName("approvedPrincipal")
    var approvedPrincipal: Double = 0.0,
    @SerializedName("proposedPrincipal")
    var proposedPrincipal: Double = 0.0,
    @SerializedName("termFrequency")
    var termFrequency: Int = 0,
    @SerializedName("termPeriodFrequencyType")
    var termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),
    @SerializedName("numberOfRepayments")
    var numberOfRepayments: Int = 0,
    @SerializedName("repaymentEvery")
    var repaymentEvery: Int = 0,
    @SerializedName("repaymentFrequencyType")
    var repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),
    @SerializedName("interestRatePerPeriod")
    var interestRatePerPeriod: Double = 0.0,
    @SerializedName("interestRateFrequencyType")
    var interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),
    @SerializedName("annualInterestRate")
    var annualInterestRate: Double = 0.0,
    @SerializedName("isFloatingInterestRate")
    var isFloatingInterestRate: Boolean = false,
    @SerializedName("amortizationType")
    var amortizationType: AmortizationType = AmortizationType(),
    @SerializedName("interestType")
    var interestType: InterestType = InterestType(),
    @SerializedName("interestCalculationPeriodType")
    var interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),
    @SerializedName("allowPartialPeriodInterestCalcualtion")
    var allowPartialPeriodInterestCalcualtion: Boolean = false,
    @SerializedName("transactionProcessingStrategyId")
    var transactionProcessingStrategyId: Int = 0,
    @SerializedName("graceOnArrearsAgeing")
    var graceOnArrearsAgeing: Int = 0,
    @SerializedName("timeline")
    var timeline: Timeline = Timeline(),
    @SerializedName("productOptions")
    var productOptions: List<ProductOptions> = ArrayList(),
    @SerializedName("datatables")
    var dataTables: ArrayList<DataTable> = ArrayList(),
    @SerializedName("loanOfficerOptions")
    var loanOfficerOptions: List<LoanOfficerOptions> = ArrayList(),
    @SerializedName("loanPurposeOptions")
    var loanPurposeOptions: List<LoanPurposeOptions> = ArrayList(),
    @SerializedName("fundOptions")
    var fundOptions: List<FundOptions> = ArrayList(),
    @SerializedName("termFrequencyTypeOptions")
    var termFrequencyTypeOptions: List<TermFrequencyTypeOptions> = ArrayList(),
    @SerializedName("repaymentFrequencyTypeOptions")
    var repaymentFrequencyTypeOptions: List<RepaymentFrequencyTypeOptions> = ArrayList(),
    @SerializedName("repaymentFrequencyNthDayTypeOptions")
    var repaymentFrequencyNthDayTypeOptions: List<RepaymentFrequencyNthDayTypeOptions> = ArrayList(),
    @SerializedName("repaymentFrequencyDaysOfWeekTypeOptions")
    var repaymentFrequencyDaysOfWeekTypeOptions: List<RepaymentFrequencyDaysOfWeekTypeOptions> = ArrayList(),
    @SerializedName("interestRateFrequencyTypeOptions")
    var interestRateFrequencyTypeOptions: List<InterestRateFrequencyTypeOptions> = ArrayList(),
    @SerializedName("amortizationTypeOptions")
    var amortizationTypeOptions: List<AmortizationTypeOptions> = ArrayList(),
    @SerializedName("interestTypeOptions")
    var interestTypeOptions: List<InterestTypeOptions> = ArrayList(),
    @SerializedName("interestCalculationPeriodTypeOptions")
    var interestCalculationPeriodTypeOptions: List<InterestCalculationPeriodType> = ArrayList(),
    @SerializedName("transactionProcessingStrategyOptions")
    var transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions> = ArrayList(),
    @SerializedName("chargeOptions")
    var chargeOptions: List<ChargeOptions> = ArrayList(),
    @SerializedName("loanCollateralOptions")
    var loanCollateralOptions: List<LoanCollateralOptions> = ArrayList(),
    @SerializedName("multiDisburseLoan")
    var multiDisburseLoan: Boolean = false,
    @SerializedName("canDefineInstallmentAmount")
    var canDefineInstallmentAmount: Boolean = false,
    @SerializedName("canDisburse")
    var canDisburse: Boolean = false,
    @SerializedName("product")
    var product: Product = Product(),
    @SerializedName("daysInMonthType")
    var daysInMonthType: DaysInMonthType = DaysInMonthType(),
    @SerializedName("daysInYearType")
    var daysInYearType: DaysInYearType = DaysInYearType(),
    @SerializedName("isInterestRecalculationEnabled")
    var isInterestRecalculationEnabled: Boolean = false,
    @SerializedName("isVariableInstallmentsAllowed")
    var isVariableInstallmentsAllowed: Boolean = false,
    @SerializedName("minimumGap")
    var minimumGap: Int = 0,
    @SerializedName("maximumGap")
    var maximumGap: Int = 0,
    @SerializedName("accountLinkingOptions")
    var accountLinkingOptions: List<AccountLinkingOptions> = ArrayList()
) : Parcelable {
    // Required to set default value to the Fund spinner
    fun getFundNameFromId(fundId: Int): Int {
        for (i in fundOptions.indices) {
            if (fundOptions[i].id == fundId) {
                return i
            }
        }
        return 0
    }
}