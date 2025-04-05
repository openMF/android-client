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

import com.mifos.core.model.objects.account.loan.AmortizationType
import com.mifos.core.model.objects.account.loan.InterestCalculationPeriodType
import com.mifos.core.model.objects.account.loan.InterestRateFrequencyType
import com.mifos.core.model.objects.account.loan.InterestType
import com.mifos.core.model.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.account.loan.TermPeriodFrequencyType
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import com.mifos.room.utils.ColumnInfo
import com.mifos.room.utils.Entity
import com.mifos.room.utils.ForeignKey
import com.mifos.room.utils.ForeignKeyAction
import com.mifos.room.utils.INHERIT_FIELD_NAME
import com.mifos.room.utils.PrimaryKey
import com.mifos.room.utils.UNDEFINED
import com.mifos.room.utils.UNSPECIFIED
import com.mifos.room.utils.VALUE_UNSPECIFIED
import kotlinx.serialization.Serializable

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
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = LoanStatusEntity::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = LoanTimelineEntity::class,
            parentColumns = ["loanId"],
            childColumns = ["timeline"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = LoansAccountSummaryEntity::class,
            parentColumns = ["loanId"],
            childColumns = ["summary"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
    ],
)
@Serializable
data class LoanWithAssociationsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val accountNo: String = "",

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val status: LoanStatusEntity = LoanStatusEntity(),

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

    val loanType: LoanTypeEntity = LoanTypeEntity(),

    @IgnoredOnParcel
    val currency: SavingAccountCurrencyEntity = SavingAccountCurrencyEntity(),

    val principal: Double = 0.0,

    val approvedPrincipal: Double = 0.0,

    val termFrequency: Int = 0,

    @IgnoredOnParcel
    val termPeriodFrequencyType: TermPeriodFrequencyType = TermPeriodFrequencyType(),

    val numberOfRepayments: Int = 0,

    val repaymentEvery: Int = 0,

    @IgnoredOnParcel
    val repaymentFrequencyType: RepaymentFrequencyType = RepaymentFrequencyType(),

    val interestRatePerPeriod: Double = 0.0,

    @IgnoredOnParcel
    val interestRateFrequencyType: InterestRateFrequencyType = InterestRateFrequencyType(),

    val annualInterestRate: Double = 0.0,

    @IgnoredOnParcel
    val amortizationType: AmortizationType = AmortizationType(),

    @IgnoredOnParcel
    val interestType: InterestType = InterestType(),

    @IgnoredOnParcel
    val interestCalculationPeriodType: InterestCalculationPeriodType = InterestCalculationPeriodType(),

    val transactionProcessingStrategyId: Int = 0,

    val transactionProcessingStrategyName: String = "",

    val syncDisbursementWithMeeting: Boolean = false,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val timeline: LoanTimelineEntity = LoanTimelineEntity(),

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val summary: LoansAccountSummaryEntity = LoansAccountSummaryEntity(),

    @IgnoredOnParcel
    val repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),

    @IgnoredOnParcel
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
