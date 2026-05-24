/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.loan

import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.objects.account.loan.Type
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanAccountSummary
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanStatus
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanTimeline
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanType
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.model.objects.account.loan.loanWithAssociations.SavingAccountCurrency
import com.mifos.core.network.dto.loan.LoanCurrencyDto
import com.mifos.core.network.dto.loan.LoanRepaymentScheduleDto
import com.mifos.core.network.dto.loan.LoanStatusDto
import com.mifos.core.network.dto.loan.LoanSummaryDto
import com.mifos.core.network.dto.loan.LoanTimelineDto
import com.mifos.core.network.dto.loan.LoanTransactionDto
import com.mifos.core.network.dto.loan.LoanTypeDto
import com.mifos.core.network.dto.loan.LoanWithAssociationsDto

fun LoanWithAssociationsDto.toDomain(): LoanWithAssociations = LoanWithAssociations(
    id = this.id.toInt(),
    accountNo = this.accountNo,
    status = this.status?.toDomain(),
    clientId = this.clientId.toInt(),
    clientName = this.clientName,
    loanProductId = this.loanProductId.toInt(),
    loanProductName = this.loanProductName,
    loanProductDescription = this.loanProductDescription,
    loanOfficerId = this.loanOfficerId?.toInt(),
    loanOfficerName = this.loanOfficerName,
    loanType = this.loanType?.toDomain(),
    currency = this.currency?.toDomain(),
    principal = this.principal,
    approvedPrincipal = this.approvedPrincipal,
    termFrequency = this.termFrequency,
    numberOfRepayments = this.numberOfRepayments,
    annualInterestRate = this.annualInterestRate,
    timeline = this.timeline?.toDomain(),
    summary = this.summary?.toDomain(),
    repaymentSchedule = this.repaymentSchedule?.toDomain(),
    transactions = this.transactions?.map { it.toDomain() },
)

fun LoanStatusDto.toDomain() = LoanStatus(
    id = this.id,
    code = this.code,
    value = this.value,
    active = this.active,
    closed = this.closed,
    overpaid = this.overpaid,
)

fun LoanTypeDto.toDomain() = LoanType(
    id = this.id,
    code = this.code,
    value = this.value,
)

fun LoanCurrencyDto.toDomain() = SavingAccountCurrency(
    code = this.code,
    name = this.name,
    decimalPlaces = this.decimalPlaces,
    displaySymbol = this.displaySymbol,
    displayLabel = this.displayLabel,
)

fun LoanTimelineDto.toDomain() = LoanTimeline(
    submittedOnDate = this.submittedOnDate,
    approvedOnDate = this.approvedOnDate,
    expectedDisbursementDate = this.expectedDisbursementDate,
    actualDisbursementDate = this.actualDisbursementDate,
    expectedMaturityDate = this.expectedMaturityDate,
)

fun LoanSummaryDto.toDomain() = LoanAccountSummary(
    principalDisbursed = this.totalPrincipal,
    principalPaid = this.principalPaid,
    principalOutstanding = this.principalOutstanding,
    totalExpectedRepayment = this.totalExpectedRepayment,
    totalRepayment = this.totalRepayment,
    totalOutstanding = this.totalOutstanding,
)

fun LoanRepaymentScheduleDto.toDomain() = RepaymentSchedule(
    loanTermInDays = this.loanTermInDays,
    totalPrincipalDisbursed = this.totalPrincipalDisbursed,
    totalOutstanding = this.totalOutstanding,
    periods = this.periods?.map {
        Period(
            period = it.period,
            dueDate = it.dueDate,
            principalDue = it.principalDue,
            principalPaid = it.principalPaid,
            principalOutstanding = it.principalOutstanding,
            totalDueForPeriod = it.totalDueForPeriod,
            totalPaidForPeriod = it.totalPaidForPeriod,
            totalOutstandingForPeriod = it.totalOutstandingForPeriod,
            complete = it.complete,
        )
    },
)

fun LoanTransactionDto.toDomain() = Transaction(
    id = this.id.toInt(),
    date = this.date?.let { ArrayList(it) } ?: ArrayList(),
    amount = this.amount,
    outstandingLoanBalance = this.outstandingLoanBalance,
    manuallyReversed = this.manuallyReversed,
    type = this.type?.let {
        Type(
            id = it.id,
            code = it.code,
            value = it.value,
            disbursement = it.disbursement,
            repayment = it.repayment,
        )
    },
)
