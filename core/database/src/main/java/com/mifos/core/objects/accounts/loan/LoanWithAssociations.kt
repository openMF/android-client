/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Table(database = MifosDatabase::class)
@ModelContainer
@Parcelize
data class LoanWithAssociations(
    @PrimaryKey
    var id: Int = 0,

    @Column
    var accountNo: String = "",

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var status: Status = Status(),

    var clientId: Int = 0,

    @Column
    var clientName: String = "",

    var clientOfficeId: Int = 0,

    var loanProductId: Int = 0,

    @Column
    var loanProductName: String = "",

    var loanProductDescription: String = "",

    var fundId: Int = 0,

    var fundName: String = "",

    var loanPurposeId: Int = 0,

    var loanPurposeName: String = "",

    var loanOfficerId: Int = 0,

    @Column
    var loanOfficerName: String = "",

    var loanType: LoanType = LoanType(),

    var currency: Currency = Currency(),

    var principal: Double = 0.0,

    var approvedPrincipal: Double = 0.0,

    var termFrequency: Int = 0,

    var termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),

    var numberOfRepayments: Int = 0,

    var repaymentEvery: Int = 0,

    var repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),

    var interestRatePerPeriod: Double = 0.0,

    var interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),

    var annualInterestRate: Double = 0.0,

    var amortizationType: AmortizationType = AmortizationType(),

    var interestType: InterestType = InterestType(),

    var interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),

    var transactionProcessingStrategyId: Int = 0,

    var transactionProcessingStrategyName: String = "",

    var syncDisbursementWithMeeting: Boolean = false,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var timeline: Timeline = Timeline(),

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var summary: Summary = Summary(),

    var repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),

    var transactions: List<Transaction> = ArrayList(),

    var feeChargesAtDisbursementCharged: Double = 0.0,

    var totalOverpaid: Double = 0.0,

    var loanCounter: Int = 0,

    var loanProductCounter: Int = 0,

    var multiDisburseLoan: Boolean = false,

    var canDisburse: Boolean = false,

    var inArrears: Boolean = false,

    var isNPA: Boolean = false,
) : MifosBaseModel(), Parcelable {

    fun isInArrears(): Boolean {
        return inArrears
    }
}