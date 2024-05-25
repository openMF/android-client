/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 2/20/2016.
 */
@Parcelize
data class GroupLoanPayload(
    var isAllowPartialPeriodInterestCalcualtion: Boolean? = null,
    var amortizationType: Int? = null,
    var groupId: Int? = null,
    var dateFormat: String? = null,

    /* public String getDisbursementData() {
       return disbursementData;
   }

   public void setDisbursementData(String disbursementData) {
       this.disbursementData = disbursementData;
   }*/
    var expectedDisbursementDate: String? = null,
    var interestCalculationPeriodType: Int? = null,
    var interestRatePerPeriod: Double? = null,
    var interestType: Int? = null,
    var loanTermFrequency: Int? = null,
    var loanTermFrequencyType: Int? = null,
    var repaymentFrequencyDayOfWeekType: Int? = null,
    var repaymentFrequencyNthDayType: Int? = null,
    var loanType: String? = null,
    var locale: String? = null,
    var numberOfRepayments: String? = null,
    var principal: String? = null,
    var productId: Int? = null,
    var repaymentEvery: String? = null,
    var repaymentFrequencyType: Int? = null,
    var submittedOnDate: String? = null,
    var transactionProcessingStrategyId: Int? = null,
    var loanPurposeId: Int? = null,
    var linkAccountId: Int? = null
) : Parcelable