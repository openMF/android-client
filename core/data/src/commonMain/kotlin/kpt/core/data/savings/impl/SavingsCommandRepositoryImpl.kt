/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.savings.impl

import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.mutation.CommandSpec
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.base.store.mutation.MutationPolicy
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.savings.SavingsCommandRepository
import kpt.core.database.savings.entity.SavingsAccountTransactionRequestEntity
import kpt.core.model.objects.account.loan.SavingsApproval
import kpt.core.model.objects.account.saving.SavingsActivationInput
import kpt.core.model.objects.account.saving.SavingsApprovalInput
import kpt.core.model.objects.account.saving.SavingsTransactionInput
import kpt.core.model.objects.payloads.SavingsPayload
import kpt.core.network.mifos.savings.api.FixedDepositApi
import kpt.core.network.mifos.savings.api.SavingsAccountApi
import kpt.core.network.mifos.savings.api.ShareAccountApi
import kpt.core.network.mifos.savings.dto.FixedDepositPayload
import kpt.core.network.mifos.savings.dto.ShareAccountPayload

@RepositoryBinding(binds = SavingsCommandRepository::class)
internal class SavingsCommandRepositoryImpl(
    private val fixedDepositApi: FixedDepositApi,
    private val savingsAccountApi: SavingsAccountApi,
    private val shareAccountApi: ShareAccountApi,
    private val gateway: MutationGateway,
) : SavingsCommandRepository {

    override suspend fun createFixedDepositAccount(fixedDepositPayload: FixedDepositPayload) =
        online { fixedDepositApi.createFixedDepositAccount(fixedDepositPayload = fixedDepositPayload) }

    override suspend fun processTransaction(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String,
        input: SavingsTransactionInput,
    ) = online {
        savingsAccountApi.processTransaction(
            savingsAccountType = savingsAccountType,
            savingsAccountId = savingsAccountId,
            transactionType = transactionType,
            savingsAccountTransactionRequest = input.toEntity(),
        )
    }

    override suspend fun activateSavings(
        savingsAccountId: Int,
        input: SavingsActivationInput,
    ) = online {
        savingsAccountApi.activateSavings(
            savingsAccountId = savingsAccountId,
            // The endpoint's body is a string map, so the map is built HERE from a typed input
            // rather than at the call site — which is what guarantees all three keys are present
            // and spelled right.
            genericRequest = hashMapOf(
                "activatedOnDate" to input.activatedOnDate,
                "locale" to input.locale,
                "dateFormat" to input.dateFormat,
            ),
        )
    }

    override suspend fun approveSavingsApplication(
        savingsAccountId: Int,
        input: SavingsApprovalInput,
    ) = online {
        savingsAccountApi.approveSavingsApplication(
            savingsAccountId = savingsAccountId,
            savingsApproval = SavingsApproval(
                approvedOnDate = input.approvedOnDate,
                locale = input.locale,
                dateFormat = input.dateFormat,
                note = input.note,
            ),
        )
    }

    override suspend fun createSavingsAccount(savingsPayload: SavingsPayload?) =
        online { savingsAccountApi.createSavingsAccount(savingsPayload = savingsPayload) }

    override suspend fun createShareAccount(shareAccountPayload: ShareAccountPayload) =
        online { shareAccountApi.createShareAccount(shareAccountPayload = shareAccountPayload) }

    /**
     * Commands run [MutationPolicy.OnlineRequired]: they await the server and write nothing
     * offline, yielding `Blocked(OFFLINE)` rather than a local success the officer would act on.
     *
     * The call is closed over rather than threaded through [CommandSpec.payload]: the payload hook
     * exists for `localApply` / `rollback` / `conflictOf`, none of which apply to a write that never
     * lands locally. It also keeps the 28 endpoints whose body is nullable usable, which
     * `CommandSpec<P : Any>` would otherwise reject.
     */
    private suspend fun <R : Any> online(endpoint: suspend () -> R): MutationResult<R> =
        gateway.command(
            CommandSpec(payload = Unit, endpoint = { endpoint() }),
            policy = MutationPolicy.OnlineRequired,
        )
}

/**
 * The `core/model` input -> Room-entity request body the savings transaction endpoint takes.
 *
 * `SavingsAccountTransactionRequestEntity` is the wire type the API declares, so the mapping lives
 * at this boundary. `id`/`savingAccountId`/`savingsAccountType`/`transactionType` are left unset:
 * they are STORAGE columns on that entity (it doubles as the offline-capture row), and the request
 * carries the account and the command in its path and query instead.
 *
 * `errorMessage` is likewise storage — a request body has no business carrying one.
 */
private fun SavingsTransactionInput.toEntity() = SavingsAccountTransactionRequestEntity(
    transactionDate = transactionDate,
    transactionAmount = transactionAmount,
    locale = locale,
    dateFormat = dateFormat,
    paymentTypeId = paymentTypeId?.toString(),
    note = note,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
)
