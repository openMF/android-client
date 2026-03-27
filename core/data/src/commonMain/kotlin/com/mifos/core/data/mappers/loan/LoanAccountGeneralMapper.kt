/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.loan

import com.mifos.core.model.entity.loan.ActualDisbursementDate
import com.mifos.core.model.entity.loan.LoanStatus
import com.mifos.core.model.entity.loan.LoanTimeline
import com.mifos.core.model.entity.loan.LoanType
import com.mifos.core.model.entity.loan.LoanWithAssociations
import com.mifos.core.model.entity.loan.LoansAccountSummary
import com.mifos.core.model.entity.loan.SavingAccountCurrency
import com.mifos.room.entities.accounts.loans.ActualDisbursementDateEntity
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanTypeEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity

fun LoanWithAssociationsEntity.toModel(): LoanWithAssociations {
    return LoanWithAssociations(
        id = id,
        accountNo = accountNo,
        status = status.toModel(),
        clientId = clientId,
        clientName = clientName,
        clientOfficeId = clientOfficeId,
        loanProductId = loanProductId,
        loanProductName = loanProductName,
        loanProductDescription = loanProductDescription,
        fundId = fundId,
        fundName = fundName,
        loanPurposeId = loanPurposeId,
        loanPurposeName = loanPurposeName,
        loanOfficerId = loanOfficerId,
        loanOfficerName = loanOfficerName,
        loanType = loanType.toModel(),
        currency = currency.toModel(),
        principal = principal,
        approvedPrincipal = approvedPrincipal,
        termFrequency = termFrequency,
        termPeriodFrequencyType = termPeriodFrequencyType,
        numberOfRepayments = numberOfRepayments,
        repaymentEvery = repaymentEvery,
        repaymentFrequencyType = repaymentFrequencyType,
        interestRatePerPeriod = interestRatePerPeriod,
        interestRateFrequencyType = interestRateFrequencyType,
        annualInterestRate = annualInterestRate,
        amortizationType = amortizationType,
        interestType = interestType,
        interestCalculationPeriodType = interestCalculationPeriodType,
        transactionProcessingStrategyId = transactionProcessingStrategyId,
        transactionProcessingStrategyName = transactionProcessingStrategyName,
        syncDisbursementWithMeeting = syncDisbursementWithMeeting,
        timeline = timeline.toModel(),
        summary = summary.toModel(),
        repaymentSchedule = repaymentSchedule,
        transactions = transactions,
        feeChargesAtDisbursementCharged = feeChargesAtDisbursementCharged,
        totalOverpaid = totalOverpaid,
        loanCounter = loanCounter,
        loanProductCounter = loanProductCounter,
        multiDisburseLoan = multiDisburseLoan,
        canDisburse = canDisburse,
        inArrears = inArrears,
        isNPA = isNPA,
    )
}

private fun LoanStatusEntity.toModel(): LoanStatus {
    return LoanStatus(
        id = id,
        code = code,
        value = value,
        pendingApproval = pendingApproval,
        waitingForDisbursal = waitingForDisbursal,
        active = active,
        closedObligationsMet = closedObligationsMet,
        closedWrittenOff = closedWrittenOff,
        closedRescheduled = closedRescheduled,
        closed = closed,
        overpaid = overpaid,
    )
}

private fun LoanTypeEntity.toModel(): LoanType {
    return LoanType(
        id = id,
        code = code,
        value = value,
    )
}

private fun SavingAccountCurrencyEntity.toModel(): SavingAccountCurrency {
    return SavingAccountCurrency(
        id = id,
        code = code,
        name = name,
        decimalPlaces = decimalPlaces,
        inMultiplesOf = inMultiplesOf,
        displaySymbol = displaySymbol,
        nameCode = nameCode,
        displayLabel = displayLabel,
    )
}

private fun LoanTimelineEntity.toModel(): LoanTimeline {
    return LoanTimeline(
        loanId = loanId,
        submittedOnDate = submittedOnDate,
        submittedByUsername = submittedByUsername,
        submittedByFirstname = submittedByFirstname,
        submittedByLastname = submittedByLastname,
        approvedOnDate = approvedOnDate,
        approvedByUsername = approvedByUsername,
        approvedByFirstname = approvedByFirstname,
        approvedByLastname = approvedByLastname,
        expectedDisbursementDate = expectedDisbursementDate,
        actualDisburseDate = actualDisburseDate?.toModel(),
        actualDisbursementDate = actualDisbursementDate,
        disbursedByUsername = disbursedByUsername,
        disbursedByFirstname = disbursedByFirstname,
        disbursedByLastname = disbursedByLastname,
        closedOnDate = closedOnDate,
        expectedMaturityDate = expectedMaturityDate,
    )
}

private fun ActualDisbursementDateEntity.toModel(): ActualDisbursementDate {
    return ActualDisbursementDate(
        loanId = loanId,
        year = year,
        month = month,
        date = date,
    )
}

private fun LoansAccountSummaryEntity.toModel(): LoansAccountSummary {
    return LoansAccountSummary(
        loanId = loanId,
        currency = currency?.toModel(),
        principalDisbursed = principalDisbursed,
        principalPaid = principalPaid,
        principalWrittenOff = principalWrittenOff,
        principalOutstanding = principalOutstanding,
        principalOverdue = principalOverdue,
        interestCharged = interestCharged,
        interestPaid = interestPaid,
        interestWaived = interestWaived,
        interestWrittenOff = interestWrittenOff,
        interestOutstanding = interestOutstanding,
        interestOverdue = interestOverdue,
        feeChargesCharged = feeChargesCharged,
        feeChargesDueAtDisbursementCharged = feeChargesDueAtDisbursementCharged,
        feeChargesPaid = feeChargesPaid,
        feeChargesWaived = feeChargesWaived,
        feeChargesWrittenOff = feeChargesWrittenOff,
        feeChargesOutstanding = feeChargesOutstanding,
        feeChargesOverdue = feeChargesOverdue,
        penaltyChargesCharged = penaltyChargesCharged,
        penaltyChargesPaid = penaltyChargesPaid,
        penaltyChargesWaived = penaltyChargesWaived,
        penaltyChargesWrittenOff = penaltyChargesWrittenOff,
        penaltyChargesOutstanding = penaltyChargesOutstanding,
        penaltyChargesOverdue = penaltyChargesOverdue,
        totalExpectedRepayment = totalExpectedRepayment,
        totalRepayment = totalRepayment,
        totalExpectedCostOfLoan = totalExpectedCostOfLoan,
        totalCostOfLoan = totalCostOfLoan,
        totalWaived = totalWaived,
        totalWrittenOff = totalWrittenOff,
        totalOutstanding = totalOutstanding,
        totalOverdue = totalOverdue,
        overdueSinceDate = overdueSinceDate,
    )
}
