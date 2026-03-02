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

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.loan.LoanStatus
import com.mifos.core.model.objects.account.loan.LoanSummary
import com.mifos.core.model.objects.account.loan.LoanTimeline
import com.mifos.core.model.objects.account.loan.LoanWithAssociations
import com.mifos.core.network.data.AbstractMapper
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity

object LoanWithAssociationsMapper :
    AbstractMapper<LoanWithAssociationsEntity, LoanWithAssociations>() {

    override fun mapFromEntity(entity: LoanWithAssociationsEntity): LoanWithAssociations {
        return LoanWithAssociations(
            id = entity.id,
            accountNo = entity.accountNo,
            status = entity.status.toDomain(),
            clientId = entity.clientId,
            clientName = entity.clientName,
            clientOfficeId = entity.clientOfficeId,
            loanOfficerName = entity.loanOfficerName,
            loanProductId = entity.loanProductId,
            loanProductName = entity.loanProductName,
            loanProductDescription = entity.loanProductDescription,
            currency = entity.currency.toDomain(),
            principal = entity.principal,
            approvedPrincipal = entity.approvedPrincipal,
            termFrequency = entity.termFrequency,
            numberOfRepayments = entity.numberOfRepayments,
            repaymentEvery = entity.repaymentEvery,
            interestRatePerPeriod = entity.interestRatePerPeriod,
            annualInterestRate = entity.annualInterestRate,
            transactionProcessingStrategyId = entity.transactionProcessingStrategyId,
            transactionProcessingStrategyName = entity.transactionProcessingStrategyName,
            syncDisbursementWithMeeting = entity.syncDisbursementWithMeeting,
            timeline = entity.timeline.toDomain(),
            loanSummary = entity.summary.toDomain(),
            feeChargesAtDisbursementCharged = entity.feeChargesAtDisbursementCharged,
            loanProductCounter = entity.loanProductCounter,
            multiDisburseLoan = entity.multiDisburseLoan,
            canDisburse = entity.canDisburse,
            inArrears = entity.inArrears,
            npa = entity.isNPA,
            repaymentFrequencyType = entity.repaymentFrequencyType.value,
            amortizationType = entity.amortizationType.value,
            isEqualAmortization = entity.isEqualAmortization,
            interestType = entity.interestType.value,
            interestCalculationPeriodType = entity.interestCalculationPeriodType.value,
            fundId = entity.fundId,
            fundName = entity.fundName,
            loanPurposeId = entity.loanPurposeId,
            loanPurposeName = entity.loanPurposeName,
            loanOfficerId = entity.loanOfficerId,
            chargedOff = entity.chargedOff,
            allowPartialPeriodInterestCalculation = entity.allowPartialPeriodInterestCalculation,
            interestRecognitionOnDisbursementDate = entity.interestRecognitionOnDisbursementDate,
            enableDownPayment = entity.enableDownPayment,
            enableIncomeCapitalization = entity.enableIncomeCapitalization,
            enableBuyDownFee = entity.enableBuyDownFee,
            enableInstallmentLevelDelinquency = entity.enableInstallmentLevelDelinquency,
            isInterestRecalculationEnabled = entity.isInterestRecalculationEnabled,
            chargeOffBehaviour = entity.chargeOffBehaviour?.value,
            daysInYearType = entity.daysInYearType?.value,
            daysInMonthType = entity.daysInMonthType?.value,
            availableDisbursementAmount = (entity.principal - entity.approvedPrincipal),
            totalOverpaid = entity.totalOverpaid,
        )
    }

    override fun mapToEntity(domainModel: LoanWithAssociations): LoanWithAssociationsEntity {
        error("Not implemented yet")
    }
}

fun LoanStatusEntity?.toDomain(): LoanStatus {
    return when {
        this == null -> LoanStatus.UNKNOWN
        pendingApproval == true -> LoanStatus.PENDING
        waitingForDisbursal == true -> LoanStatus.APPROVED
        active == true -> LoanStatus.ACTIVE
        overpaid == true -> LoanStatus.OVERPAID
        closedWrittenOff == true -> LoanStatus.CLOSED_WRITTEN_OFF
        closedObligationsMet == true -> LoanStatus.CLOSED_OBLIGATIONS_MET
        closedRescheduled == true -> LoanStatus.CLOSED_RESCHEDULED
        closed == true -> LoanStatus.CLOSED
        else -> LoanStatus.UNKNOWN
    }
}

private fun SavingAccountCurrencyEntity?.toDomain(): Currency? {
    return this?.let {
        Currency(
            code = code,
            name = name,
            nameCode = nameCode,
            decimalPlaces = decimalPlaces,
            displaySymbol = displaySymbol,
            displayLabel = displayLabel,
        )
    }
}

private fun LoanTimelineEntity?.toDomain(): LoanTimeline? {
    return this?.let {
        LoanTimeline(
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
            actualDisbursementDate = actualDisbursementDate,
            disbursedByUsername = disbursedByUsername,
            disbursedByFirstname = disbursedByFirstname,
            disbursedByLastname = disbursedByLastname,
            closedOnDate = closedOnDate,
            expectedMaturityDate = expectedMaturityDate,
        )
    }
}

private fun LoansAccountSummaryEntity?.toDomain(): LoanSummary? {
    return this?.let {
        LoanSummary(
            loanId = loanId,
            currency = currency?.toDomain(),
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
}
