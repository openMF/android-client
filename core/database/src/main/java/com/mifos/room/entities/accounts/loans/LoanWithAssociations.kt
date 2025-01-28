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

import android.os.Parcelable
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
import kotlinx.parcelize.Parcelize

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

@Parcelize
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
            parentColumns = ["loanId"],
            childColumns = ["summary"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class LoanWithAssociations(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "accountNo")
    val accountNo: String = "",

    @ColumnInfo(name = "status", index = true)
    val status: Status = Status(),

    @ColumnInfo(name = "clientId")
    val clientId: Int = 0,

    @ColumnInfo(name = "clientName")
    val clientName: String = "",

    @ColumnInfo(name = "clientOfficeId")
    val clientOfficeId: Int = 0,

    @ColumnInfo(name = "loanProductId")
    val loanProductId: Int = 0,

    @ColumnInfo(name = "loanProductName")
    val loanProductName: String = "",

    @ColumnInfo(name = "loanProductDescription")
    val loanProductDescription: String = "",

    @ColumnInfo(name = "fundId")
    val fundId: Int = 0,

    @ColumnInfo(name = "fundName")
    val fundName: String = "",

    @ColumnInfo(name = "loanPurposeId")
    val loanPurposeId: Int = 0,

    @ColumnInfo(name = "loanPurposeName")
    val loanPurposeName: String = "",

    @ColumnInfo(name = "loanOfficerId")
    val loanOfficerId: Int = 0,

    @ColumnInfo(name = "loanOfficerName")
    val loanOfficerName: String = "",

    @Embedded(prefix = "LoanType_")
    val loanType: LoanType = LoanType(),

    @Embedded(prefix = "Currency_")
    val currency: Currency = Currency(),

    @ColumnInfo(name = "principal")
    val principal: Double = 0.0,

    @ColumnInfo(name = "approvedPrincipal")
    val approvedPrincipal: Double = 0.0,

    @ColumnInfo(name = "termFrequency")
    val termFrequency: Int = 0,

    @Embedded(prefix = "termPeriodFrequencyType_")
    val termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),

    @ColumnInfo(name = "numberOfRepayments")
    val numberOfRepayments: Int = 0,

    @ColumnInfo(name = "repaymentEvery")
    val repaymentEvery: Int = 0,

    @Embedded(prefix = "repaymentFrequencyType_")
    val repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),

    @ColumnInfo(name = "interestRatePerPeriod")
    val interestRatePerPeriod: Double = 0.0,

    @Embedded(prefix = "interestRateFrequencyType_")
    val interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),

    @ColumnInfo(name = "annualInterestRate")
    val annualInterestRate: Double = 0.0,

    @Embedded(prefix = "amortization_type_")
    val amortizationType: AmortizationType = AmortizationType(),

    @Embedded(prefix = "interestType_")
    val interestType: InterestType = InterestType(),

    @Embedded(prefix = "interestCalculationPeriodType_")
    val interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),

    @ColumnInfo(name = "transactionProcessingStrategyId")
    val transactionProcessingStrategyId: Int = 0,

    @ColumnInfo(name = "transactionProcessingStrategyName")
    val transactionProcessingStrategyName: String = "",

    @ColumnInfo(name = "syncDisbursementWithMeeting")
    val syncDisbursementWithMeeting: Boolean = false,

    @ColumnInfo(name = "timeline", index = true)
    val timeline: Timeline = Timeline(),

    @ColumnInfo(name = "summary", index = true)
    val summary: Summary = Summary(),

    @Embedded(prefix = "repaymentSchedule_")
    val repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),

    @ColumnInfo(name = "transactions")
    val transactions: List<Transaction> = ArrayList(),

    @ColumnInfo(name = "feeChargesAtDisbursementCharged")
    val feeChargesAtDisbursementCharged: Double = 0.0,

    @ColumnInfo(name = "totalOverpaid")
    val totalOverpaid: Double = 0.0,

    @ColumnInfo(name = "loanCounter")
    val loanCounter: Int = 0,

    @ColumnInfo(name = "loanProductCounter")
    val loanProductCounter: Int = 0,

    @ColumnInfo(name = "multiDisburseLoan")
    val multiDisburseLoan: Boolean = false,

    @ColumnInfo(name = "canDisburse")
    val canDisburse: Boolean = false,

    @ColumnInfo(name = "inArrears")
    val inArrears: Boolean = false,

    @ColumnInfo(name = "isNPA")
    val isNPA: Boolean = false,
) : Parcelable
