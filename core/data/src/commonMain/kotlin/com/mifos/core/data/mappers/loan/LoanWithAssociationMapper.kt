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

import com.mifos.core.model.objects.account.loan.loanWithAssociations.AmortizationType
import com.mifos.core.model.objects.account.loan.loanWithAssociations.Currency
import com.mifos.core.model.objects.account.loan.loanWithAssociations.InterestCalculationPeriodType
import com.mifos.core.model.objects.account.loan.loanWithAssociations.InterestType
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanStatus
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanSummary
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanTimeline
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.model.objects.account.loan.loanWithAssociations.RepaymentFrequencyType
import com.mifos.core.network.data.AbstractMapper
import com.mifos.room.entities.accounts.loans.LoanAccountSummaryEntity
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
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
            summary = entity.summary.toDomain(),
            feeChargesAtDisbursementCharged = entity.feeChargesAtDisbursementCharged,
            loanProductCounter = entity.loanProductCounter,
            multiDisburseLoan = entity.multiDisburseLoan,
            canDisburse = entity.canDisburse,
            inArrears = entity.inArrears,
            isNPA = entity.isNPA,
            repaymentFrequencyType = entity.repaymentFrequencyType?.let { RepaymentFrequencyType(id = it.id, code = it.code, value = it.value) },
            amortizationType = entity.amortizationType?.let { AmortizationType(id = it.id, code = it.code, value = it.value) },
            isEqualAmortization = entity.isEqualAmortization,
            interestType = entity.interestType?.let { InterestType(id = it.id, code = it.code, value = it.value) },
            interestCalculationPeriodType = entity.interestCalculationPeriodType?.let { InterestCalculationPeriodType(id = it.id, code = it.code, value = it.value) },
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
            availableDisbursementAmount = (entity.principal - entity.approvedPrincipal),
            totalOverpaid = entity.totalOverpaid,
        )
    }

    override fun mapToEntity(domainModel: LoanWithAssociations): LoanWithAssociationsEntity {
        error("Not implemented yet")
    }
}

fun LoanStatusEntity?.toDomain(): LoanStatus {
    return LoanStatus(
        id = this?.id,
        code = this?.code,
        value = this?.value,
        pendingApproval = this?.pendingApproval,
        waitingForDisbursal = this?.waitingForDisbursal,
        active = this?.active,
        closedObligationsMet = this?.closedObligationsMet,
        closedWrittenOff = this?.closedWrittenOff,
        closedRescheduled = this?.closedRescheduled,
        closed = this?.closed,
        overpaid = this?.overpaid,
    )
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

private fun LoanAccountSummaryEntity?.toDomain(): LoanSummary? {
    return this?.let {
        LoanSummary(
            loanId = loanId,
            currency = currency?.toDomain(),
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
}
