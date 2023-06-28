/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data

/**
 * Created by nellyk on 2/20/2016.
 */
class GroupLoanPayload {
    var isAllowPartialPeriodInterestCalcualtion = false
    var amortizationType = 0
    var groupId = 0
    var dateFormat: String? = null

    /* public String getDisbursementData() {
       return disbursementData;
   }

   public void setDisbursementData(String disbursementData) {
       this.disbursementData = disbursementData;
   }*/
    var expectedDisbursementDate: String? = null
    var interestCalculationPeriodType = 0
    var interestRatePerPeriod: Double? = null
    var interestType = 0
    var loanTermFrequency = 0
    var loanTermFrequencyType = 0
    var repaymentFrequencyDayOfWeekType: Int? = null
    var repaymentFrequencyNthDayType: Int? = null
    var loanType: String? = null
    var locale: String? = null
    var numberOfRepayments: String? = null
    var principal: String? = null
    var productId = 0
    var repaymentEvery: String? = null
    var repaymentFrequencyType = 0
    var submittedOnDate: String? = null
    var transactionProcessingStrategyId = 0
    var loanPurposeId = 0
    var linkAccountId: Int? = null
}