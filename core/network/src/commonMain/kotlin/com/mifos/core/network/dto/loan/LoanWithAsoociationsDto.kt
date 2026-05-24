/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.dto.loan

import kotlinx.serialization.Serializable

@Serializable
data class LoanWithAssociationsDto(
    val id: Long,
    val accountNo: String? = null,
    val status: LoanStatusDto? = null,
    val clientId: Long,
    val clientAccountNo: String? = null,
    val clientName: String? = null,
    val loanProductId: Long,
    val loanProductName: String? = null,
    val loanProductDescription: String? = null,
    val loanOfficerId: Long? = null,
    val loanOfficerName: String? = null,
    val loanType: LoanTypeDto? = null,
    val currency: LoanCurrencyDto? = null,
    val principal: Double,
    val approvedPrincipal: Double,
    val netDisbursalAmount: Double,
    val termFrequency: Int,
    val numberOfRepayments: Int,
    val annualInterestRate: Double,
    val timeline: LoanTimelineDto? = null,
    val summary: LoanSummaryDto? = null,
    val repaymentSchedule: LoanRepaymentScheduleDto? = null,
    val transactions: List<LoanTransactionDto>? = emptyList(),
    val charges: List<LoanChargeDto>? = emptyList(),
    val notes: List<LoanNoteDto>? = emptyList(),
    val delinquent: LoanDelinquentDto? = null,
)

@Serializable
data class LoanStatusDto(val id: Int, val code: String? = null, val value: String? = null, val active: Boolean = false, val closed: Boolean = false, val overpaid: Boolean = false)

@Serializable
data class LoanTypeDto(val id: Int, val code: String? = null, val value: String? = null)

@Serializable
data class LoanCurrencyDto(val code: String? = null, val name: String? = null, val decimalPlaces: Int, val displaySymbol: String? = null, val displayLabel: String? = null)

@Serializable
data class LoanTimelineDto(val submittedOnDate: List<Int>? = null, val approvedOnDate: List<Int>? = null, val expectedDisbursementDate: List<Int>? = null, val actualDisbursementDate: List<Int>? = null, val expectedMaturityDate: List<Int>? = null)

@Serializable
data class LoanSummaryDto(val totalPrincipal: Double, val principalPaid: Double, val principalOutstanding: Double, val totalExpectedRepayment: Double, val totalRepayment: Double, val totalOutstanding: Double)

@Serializable
data class LoanRepaymentScheduleDto(val loanTermInDays: Int, val totalPrincipalDisbursed: Double, val totalOutstanding: Double, val periods: List<LoanPeriodDto>? = emptyList())

@Serializable
data class LoanPeriodDto(val period: Int? = null, val dueDate: List<Int>? = null, val principalDue: Double? = null, val principalPaid: Double? = null, val principalOutstanding: Double? = null, val totalDueForPeriod: Double? = null, val totalPaidForPeriod: Double? = null, val totalOutstandingForPeriod: Double? = null, val complete: Boolean? = null)

@Serializable
data class LoanTransactionDto(val id: Long, val type: LoanTransactionTypeDto? = null, val date: List<Int>? = null, val amount: Double, val outstandingLoanBalance: Double? = null, val manuallyReversed: Boolean = false)

@Serializable
data class LoanTransactionTypeDto(val id: Int, val code: String? = null, val value: String? = null, val disbursement: Boolean = false, val repayment: Boolean = false)

@Serializable
data class LoanChargeDto(val id: Long, val name: String? = null, val amount: Double, val amountPaid: Double, val amountOutstanding: Double, val paid: Boolean = false, val waived: Boolean = false)

@Serializable
data class LoanNoteDto(val id: Long, val note: String? = null, val createdOn: String? = null)

@Serializable
data class LoanDelinquentDto(val pastDueDays: Int, val delinquentAmount: Double, val nextPaymentDueDate: List<Int>? = null)
data class NoteDto(
    val id: Long,
    val note: String?,
    val createdOn: String?,
)

data class DelinquentDto(
    val pastDueDays: Int,
    val delinquentAmount: Double,
    val nextPaymentDueDate: List<Int>?,
)
