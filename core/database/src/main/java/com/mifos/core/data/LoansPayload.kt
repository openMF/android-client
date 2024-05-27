/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data

import android.os.Parcelable
import com.mifos.core.objects.noncore.DataTablePayload
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 2/20/2016.
 */
@Parcelize
class LoansPayload(
    var allowPartialPeriodInterestCalcualtion: Boolean? = null,
    var amortizationType: Int? = null,
    var clientId: Int? = null,
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
    var loanType: String? = null,
    var locale: String? = null,
    var numberOfRepayments: Int? = null,
    var principal: Double? = null,
    var productId: Int? = null,
    var repaymentEvery: Int? = null,
    var repaymentFrequencyType: Int? = null,
    var repaymentFrequencyDayOfWeekType: Int? = null,
    var repaymentFrequencyNthDayType: Int? = null,
    var submittedOnDate: String? = null,
    var transactionProcessingStrategyId: Int? = null,
    var loanPurposeId: Int? = null,
    var loanOfficerId: Int? = null,
    var fundId: Int? = null,
    var linkAccountId: Int? = null,
    var dataTables: ArrayList<DataTablePayload>? = null
) : Parcelable