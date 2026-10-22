/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.recurringdeposit.impl

import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.mutation.CommandSpec
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.base.store.mutation.MutationPolicy
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.recurringdeposit.RecurringDepositCommandRepository
import kpt.core.model.objects.payloads.ChargeItem
import kpt.core.model.objects.payloads.RecurringDepositAccountPayload
import kpt.core.model.recurringdeposit.RecurringDepositApplicationInput
import kpt.core.network.mifos.recurringdeposit.api.RecurringAccountApi

@RepositoryBinding(binds = RecurringDepositCommandRepository::class)
internal class RecurringDepositCommandRepositoryImpl(
    private val recurringAccountApi: RecurringAccountApi,
    private val gateway: MutationGateway,
) : RecurringDepositCommandRepository {

    override suspend fun createRecurringDepositAccount(
        input: RecurringDepositApplicationInput,
    ): MutationResult<Unit> = online {
        recurringAccountApi.createRecurringDepositAccount(
            recurringDepositAccountPayload = input.toPayload(),
        )
        Unit
    }

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
 * The `core/model` input -> network payload boundary.
 *
 * `monthDayFormat` and `expectedFirstDepositOnDate` are left unset: the form collects neither, and
 * the original hardcoded `expectedFirstDepositOnDate = null` for the same reason.
 *
 * Each charge's amount is parsed to the `Double` the payload declares. A row that does not parse is
 * dropped rather than sent as 0 — the form refuses to add one, so this is a type boundary rather
 * than a validation point.
 */
private fun RecurringDepositApplicationInput.toPayload() = RecurringDepositAccountPayload(
    clientId = clientId,
    productId = productId,
    submittedOnDate = submittedOnDate,
    locale = locale,
    dateFormat = dateFormat,
    mandatoryRecommendedDepositAmount = mandatoryRecommendedDepositAmount,
    recurringFrequency = recurringFrequency,
    recurringFrequencyType = recurringFrequencyType,
    depositPeriod = depositPeriod,
    depositPeriodFrequencyId = depositPeriodFrequencyId,
    fieldOfficerId = fieldOfficerId,
    externalId = externalId,
    isMandatoryDeposit = isMandatoryDeposit,
    adjustAdvanceTowardsFuturePayments = adjustAdvanceTowardsFuturePayments,
    allowWithdrawal = allowWithdrawal,
    interestCompoundingPeriodType = interestCompoundingPeriodType,
    interestPostingPeriodType = interestPostingPeriodType,
    interestCalculationType = interestCalculationType,
    interestCalculationDaysInYearType = interestCalculationDaysInYearType,
    lockinPeriodFrequency = lockinPeriodFrequency,
    lockinPeriodFrequencyType = lockinPeriodFrequencyType,
    preClosurePenalApplicable = preClosurePenalApplicable,
    preClosurePenalInterest = preClosurePenalInterest,
    preClosurePenalInterestOnTypeId = preClosurePenalInterestOnTypeId,
    charges = charges.mapNotNull { charge ->
        ChargeItem(chargeId = charge.chargeId, amount = charge.amount.toDoubleOrNull() ?: return@mapNotNull null)
    },
)
