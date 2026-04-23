/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.loan

import com.mifos.core.model.entity.loan.loanWithAssociations.ActualDisbursementDate
import com.mifos.core.model.entity.loan.loanWithAssociations.LoanStatus
import com.mifos.core.model.entity.loan.loanWithAssociations.LoanTimeline
import com.mifos.core.model.entity.loan.loanWithAssociations.LoanType
import com.mifos.core.model.entity.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.model.entity.loan.loanWithAssociations.LoansAccountSummary
import com.mifos.core.model.entity.loan.loanWithAssociations.SavingAccountCurrency
import com.mifos.core.network.data.AbstractMapper
import com.mifos.room.entities.accounts.loans.ActualDisbursementDateEntity
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanTypeEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity

object LoanAccountGeneralMapper : AbstractMapper<LoanWithAssociationsEntity, LoanWithAssociations>() {

    override fun mapFromEntity(entity: LoanWithAssociationsEntity): LoanWithAssociations {
        return LoanWithAssociations(
            id = entity.id,
            accountNo = entity.accountNo,
            status = entity.status.toModel(),
            clientId = entity.clientId,
            clientName = entity.clientName,
            clientOfficeId = entity.clientOfficeId,
            loanProductId = entity.loanProductId,
            loanProductName = entity.loanProductName,
            loanProductDescription = entity.loanProductDescription,
            fundId = entity.fundId,
            fundName = entity.fundName,
            loanPurposeId = entity.loanPurposeId,
            loanPurposeName = entity.loanPurposeName,
            loanOfficerId = entity.loanOfficerId,
            loanOfficerName = entity.loanOfficerName,
            loanType = entity.loanType.toModel(),
            currency = entity.currency.toModel(),
            principal = entity.principal,
            approvedPrincipal = entity.approvedPrincipal,
            proposedPrincipal = entity.proposedPrincipal,
            termFrequency = entity.termFrequency,
            termPeriodFrequencyType = entity.termPeriodFrequencyType,
            numberOfRepayments = entity.numberOfRepayments,
            repaymentEvery = entity.repaymentEvery,
            repaymentFrequencyType = entity.repaymentFrequencyType,
            interestRatePerPeriod = entity.interestRatePerPeriod,
            interestRateFrequencyType = entity.interestRateFrequencyType,
            annualInterestRate = entity.annualInterestRate,
            amortizationType = entity.amortizationType,
            interestType = entity.interestType,
            interestCalculationPeriodType = entity.interestCalculationPeriodType,
            transactionProcessingStrategyId = entity.transactionProcessingStrategyId,
            transactionProcessingStrategyName = entity.transactionProcessingStrategyName,
            syncDisbursementWithMeeting = entity.syncDisbursementWithMeeting,
            timeline = entity.timeline.toModel(),
            summary = entity.summary.toModel(),
            repaymentSchedule = entity.repaymentSchedule,
            transactions = entity.transactions,
            feeChargesAtDisbursementCharged = entity.feeChargesAtDisbursementCharged,
            totalOverpaid = entity.totalOverpaid,
            loanCounter = entity.loanCounter,
            loanProductCounter = entity.loanProductCounter,
            multiDisburseLoan = entity.multiDisburseLoan,
            canDisburse = entity.canDisburse,
            inArrears = entity.inArrears,
            isNPA = entity.isNPA,
        )
    }

    override fun mapToEntity(domainModel: LoanWithAssociations): LoanWithAssociationsEntity {
        throw NotImplementedError("mapToEntity not yet implemented for LoanWithAssociations")
    }
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
        principalWaived = principalWaived,
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
