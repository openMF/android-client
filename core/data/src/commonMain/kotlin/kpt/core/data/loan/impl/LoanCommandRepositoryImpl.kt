/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.loan.impl

import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.mutation.CommandSpec
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.base.store.mutation.MutationPolicy
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.loan.LoanCommandRepository
import kpt.core.database.loan.entity.LoanRepaymentRequestEntity
import kpt.core.model.objects.account.loan.LoanApplicationInput
import kpt.core.model.objects.account.loan.LoanApproval
import kpt.core.model.objects.account.loan.RepaymentSchedule
import kpt.core.model.objects.account.loan.loanDisburse.LoanDisburseInput
import kpt.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import kpt.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import kpt.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import kpt.core.model.objects.account.loan.transfer.AccountTransferRequest
import kpt.core.model.objects.payloads.GroupLoanPayload
import kpt.core.network.mifos.loan.api.LoanApi
import kpt.core.network.mifos.loan.dto.AssignLoanOfficerRequestDto
import kpt.core.network.mifos.loan.dto.GuarantorRequestDto
import kpt.core.network.mifos.loan.dto.LoanChargeOffRequestDto
import kpt.core.network.mifos.loan.dto.LoanDisburseRequestDto
import kpt.core.network.mifos.loan.dto.LoansPayload
import kpt.core.network.mifos.loan.dto.RejectLoanRequestDto
@RepositoryBinding(binds = LoanCommandRepository::class)
internal class LoanCommandRepositoryImpl(
    private val api: LoanApi,
    private val gateway: MutationGateway,
) : LoanCommandRepository {

    override suspend fun approveLoanApplication(loanId: Int, approval: LoanApproval) =
        online(approval) { api.approveLoanApplication(loanId, it) }

    override suspend fun rejectLoan(loanId: Int, request: RejectLoanRequestDto) =
        online(request) { api.rejectLoan(loanId, it) }

    /**
     * Maps the domain input to the wire DTO. This mapping is the whole reason the contract takes a
     * `core/model` type: it keeps `feature/loan` off `core:network`'s classpath.
     */
    override suspend fun disburse(loanId: Int, request: LoanDisburseInput): MutationResult<Unit> =
        online(request.toDto()) {
            api.disburse(loanId, it)
            Unit
        }

    override suspend fun submitPayment(loanId: Int, request: LoanRepaymentRequestEntity) =
        online(request) { api.submitPayment(loanId, it) }

    override suspend fun chargeOff(loanId: Int, request: LoanChargeOffRequestDto) =
        online(request) { api.chargeOff(loanId, it) }

    override suspend fun assignLoanOfficer(loanId: Int, request: AssignLoanOfficerRequestDto) =
        online(request) { api.assignLoanOfficer(loanId = loanId, request = it) }

    override suspend fun createLoanAccount(input: LoanApplicationInput): MutationResult<Unit> =
        online(input) {
            api.createLoansAccount(it.toPayload())
            Unit
        }

    override suspend fun createGroupLoanAccount(payload: GroupLoanPayload) =
        online(payload) { api.createGroupLoansAccount(it) }

    // A schedule preview changes nothing on the server, but it is a POST and needs the network —
    // OnlineRequired keeps it from being queued as if it were a mutation.

    override suspend fun calculateLoanSchedule(
        input: LoanApplicationInput,
    ): MutationResult<RepaymentSchedule> = online(input) { api.calculateLoanSchedule(it.toPayload()) }

    override suspend fun createGuarantor(loanId: Int, request: GuarantorRequestDto) =
        online(request) { api.createGuarantor(loanId, it) }

    override suspend fun submitAccountTransfer(request: AccountTransferRequest): MutationResult<Unit> =
        online(request) {
            api.submitAccountTransfer(it)
            Unit
        }

    override suspend fun submitLoanReschedule(request: LoanRescheduleRequest): MutationResult<Unit> =
        online(request) {
            api.submitLoanReschedule(it)
            Unit
        }

    override suspend fun approveLoanReschedule(
        scheduleId: Int,
        request: LoanRescheduleApprovalRequest,
    ): MutationResult<Unit> = online(request) {
        api.approveLoanReschedule(scheduleId, it)
        Unit
    }

    override suspend fun rejectLoanReschedule(
        scheduleId: Int,
        request: LoanRescheduleRejectionRequest,
    ): MutationResult<Unit> = online(request) {
        api.rejectLoanReschedule(scheduleId, it)
        Unit
    }

    /**
     * Every loan command runs [MutationPolicy.OnlineRequired]: it awaits the server and writes
     * nothing offline, yielding `Blocked(OFFLINE)` instead of a local success the officer would act
     * on. Offline capture is a separate, explicit queue — never a silent fallback from here.
     */
    private suspend fun <P : Any, R : Any> online(
        payload: P,
        endpoint: suspend (P) -> R,
    ): MutationResult<R> = gateway.command(
        CommandSpec(payload = payload, endpoint = endpoint),
        policy = MutationPolicy.OnlineRequired,
    )
}

/** The one place the disbursement's domain input becomes a wire payload. */
private fun LoanDisburseInput.toDto() = LoanDisburseRequestDto(
    actualDisbursementDate = actualDisbursementDate,
    transactionAmount = transactionAmount,
    paymentTypeId = paymentTypeId,
    note = note,
    externalId = externalId,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
    locale = locale,
    dateFormat = dateFormat,
)

/**
 * The `core/model` input -> network DTO boundary.
 *
 * Lives here rather than on the input type: `core/model` must not know about `core/network` either,
 * and this is the only place the two meet. `dataTables` is deliberately unset — no caller collects
 * them, and the original's `addLoanAccountScreen` left its datatable callback commented out.
 */
private fun LoanApplicationInput.toPayload() = LoansPayload(
    clientId = clientId,
    productId = productId,
    loanType = loanType,
    submittedOnDate = submittedOnDate,
    expectedDisbursementDate = expectedDisbursementDate,
    locale = locale,
    dateFormat = dateFormat,
    principal = principal,
    numberOfRepayments = numberOfRepayments,
    interestRatePerPeriod = interestRatePerPeriod,
    repaymentEvery = repaymentEvery,
    loanTermFrequency = loanTermFrequency,
    loanTermFrequencyType = loanTermFrequencyType,
    repaymentFrequencyType = repaymentFrequencyType,
    repaymentFrequencyDayOfWeekType = repaymentFrequencyDayOfWeekType,
    repaymentFrequencyNthDayType = repaymentFrequencyNthDayType,
    amortizationType = amortizationType,
    interestType = interestType,
    interestCalculationPeriodType = interestCalculationPeriodType,
    transactionProcessingStrategyCode = transactionProcessingStrategyCode,
    allowPartialPeriodInterestCalculation = allowPartialPeriodInterestCalculation,
    loanPurposeId = loanPurposeId,
    fundId = fundId,
    loanOfficerId = loanOfficerId,
    linkAccountId = linkAccountId,
    externalId = externalId,
)
