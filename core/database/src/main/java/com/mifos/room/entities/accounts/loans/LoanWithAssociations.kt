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
import com.mifos.core.model.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
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

    val accountNo: String = "",

    @ColumnInfo(index = true)
    val status: Status = Status(),

    val clientId: Int = 0,

    val clientName: String = "",

    val clientOfficeId: Int = 0,

    val loanProductId: Int = 0,

    val loanProductName: String = "",

    val loanProductDescription: String = "",

    val fundId: Int = 0,

    val fundName: String = "",

    val loanPurposeId: Int = 0,

    val loanPurposeName: String = "",

    val loanOfficerId: Int = 0,

    val loanOfficerName: String = "",

    @Embedded(prefix = "LoanType_")
    val loanType: LoanType = LoanType(),

    @Embedded(prefix = "Currency_")
    val currency: Currency = Currency(),

    val principal: Double = 0.0,

    val approvedPrincipal: Double = 0.0,

    val termFrequency: Int = 0,

    @Embedded(prefix = "termPeriodFrequencyType_")
    val termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),

    val numberOfRepayments: Int = 0,

    val repaymentEvery: Int = 0,

    @Embedded(prefix = "repaymentFrequencyType_")
    val repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),

    val interestRatePerPeriod: Double = 0.0,

    @Embedded(prefix = "interestRateFrequencyType_")
    val interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),

    val annualInterestRate: Double = 0.0,

    @Embedded(prefix = "amortization_type_")
    val amortizationType: AmortizationType = AmortizationType(),

    @Embedded(prefix = "interestType_")
    val interestType: InterestType = InterestType(),

    @Embedded(prefix = "interestCalculationPeriodType_")
    val interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),

    val transactionProcessingStrategyId: Int = 0,

    val transactionProcessingStrategyName: String = "",

    val syncDisbursementWithMeeting: Boolean = false,

    @ColumnInfo(index = true)
    val timeline: Timeline = Timeline(),

    @ColumnInfo(index = true)
    val summary: Summary = Summary(),

    @Embedded(prefix = "repaymentSchedule_")
    val repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),

    val transactions: List<Transaction> = emptyList(),

    val feeChargesAtDisbursementCharged: Double = 0.0,

    val totalOverpaid: Double = 0.0,

    val loanCounter: Int = 0,

    val loanProductCounter: Int = 0,

    val multiDisburseLoan: Boolean = false,

    val canDisburse: Boolean = false,

    val inArrears: Boolean = false,

    val isNPA: Boolean = false,
) : Parcelable
