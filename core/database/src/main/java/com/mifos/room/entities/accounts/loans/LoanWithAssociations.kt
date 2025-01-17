/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.core.objects.account.loan.AmortizationType
import com.mifos.core.objects.account.loan.Currency
import com.mifos.core.objects.account.loan.InterestCalculationPeriodType
import com.mifos.core.objects.account.loan.InterestRateFrequencyType
import com.mifos.core.objects.account.loan.InterestType
import com.mifos.core.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.objects.account.loan.RepaymentSchedule
import com.mifos.core.objects.account.loan.TermPeriodFrequencyType
import com.mifos.core.objects.account.loan.Transaction

@Entity(
    tableName = "LoanWithAssociations",
    foreignKeys = [
        ForeignKey(
            entity = Status::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Timeline::class,
            parentColumns = ["loanId"],
            childColumns = ["timeline"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Summary::class,
            parentColumns = ["savings"],
            childColumns = ["summary"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
// @TypeConverters(
//    AmortizationTypeConverter::class,
//    CurrencyTypeConverter::class,
//    InterestCalculationPeriodTypeConverter::class,
//    InterestRateFrequencyTypeConverter::class,
//    InterestTypeConverter::class,
//    LoanTypeConverter::class,
//    RepaymentFrequencyTypeConverter::class,
//    RepaymentScheduleTypeConverter::class,
//    TermPeriodFrequencyTypeConverter::class,
//    TimelineTypeConverter::class,
//    TransactionListConverter::class,
// )
data class LoanWithAssociations(
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo(name = "accountNo")
    var accountNo: String = "",

    @ColumnInfo(name = "status", index = true)
    var status: Status = Status(),

    @ColumnInfo(name = "clientId")
    var clientId: Int = 0,

    @ColumnInfo(name = "clientName")
    var clientName: String = "",

    @ColumnInfo(name = "clientOfficeId")
    var clientOfficeId: Int = 0,

    @ColumnInfo(name = "loanProductId")
    var loanProductId: Int = 0,

    @ColumnInfo(name = "loanProductName")
    var loanProductName: String = "",

    @ColumnInfo(name = "loanProductDescription")
    var loanProductDescription: String = "",

    @ColumnInfo(name = "fundId")
    var fundId: Int = 0,

    @ColumnInfo(name = "fundName")
    var fundName: String = "",

    @ColumnInfo(name = "loanPurposeId")
    var loanPurposeId: Int = 0,

    @ColumnInfo(name = "loanPurposeName")
    var loanPurposeName: String = "",

    @ColumnInfo(name = "loanOfficerId")
    var loanOfficerId: Int = 0,

    @ColumnInfo(name = "loanOfficerName")
    var loanOfficerName: String = "",

    @Embedded
    var loanType: LoanType = LoanType(),

    @Embedded
    var currency: Currency = Currency(),

    @ColumnInfo(name = "principal")
    var principal: Double = 0.0,

    @ColumnInfo(name = "approvedPrincipal")
    var approvedPrincipal: Double = 0.0,

    @ColumnInfo(name = "termFrequency")
    var termFrequency: Int = 0,

    @Embedded
    var termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),

    @ColumnInfo(name = "numberOfRepayments")
    var numberOfRepayments: Int = 0,

    @ColumnInfo(name = "repaymentEvery")
    var repaymentEvery: Int = 0,

    @Embedded
    var repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),

    @ColumnInfo(name = "interestRatePerPeriod")
    var interestRatePerPeriod: Double = 0.0,

    @Embedded
    var interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),

    @ColumnInfo(name = "annualInterestRate")
    var annualInterestRate: Double = 0.0,

    @Embedded
    var amortizationType: AmortizationType = AmortizationType(),

    @Embedded
    var interestType: InterestType = InterestType(),

    @Embedded
    var interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),

    @ColumnInfo(name = "transactionProcessingStrategyId")
    var transactionProcessingStrategyId: Int = 0,

    @ColumnInfo(name = "transactionProcessingStrategyName")
    var transactionProcessingStrategyName: String = "",

    @ColumnInfo(name = "syncDisbursementWithMeeting")
    var syncDisbursementWithMeeting: Boolean = false,

    @ColumnInfo(name = "timeline", index = true)
    var timeline: Timeline = Timeline(),

    @ColumnInfo(name = "summary", index = true)
    var summary: Summary = Summary(),

    @Embedded
    var repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),

    @ColumnInfo(name = "transactions")
    var transactions: List<Transaction> = ArrayList(),

    @ColumnInfo(name = "feeChargesAtDisbursementCharged")
    var feeChargesAtDisbursementCharged: Double = 0.0,

    @ColumnInfo(name = "totalOverpaid")
    var totalOverpaid: Double = 0.0,

    @ColumnInfo(name = "loanCounter")
    var loanCounter: Int = 0,

    @ColumnInfo(name = "loanProductCounter")
    var loanProductCounter: Int = 0,

    @ColumnInfo(name = "multiDisburseLoan")
    var multiDisburseLoan: Boolean = false,

    @ColumnInfo(name = "canDisburse")
    var canDisburse: Boolean = false,

    @ColumnInfo(name = "inArrears")
    var inArrears: Boolean = false,

    @ColumnInfo(name = "isNPA")
    var isNPA: Boolean = false,
)
