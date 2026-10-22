/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.testing

import kotlinx.coroutines.CompletableDeferred
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.charge.ChargeCommandRepository
import kpt.core.model.objects.payloads.ChargesPayload

/**
 * Test double for [ChargeCommandRepository] — beside the interface it fakes, same reasoning as
 * [FakeClientCommandRepository].
 *
 * Only [createCharges] is implemented; delete and update THROW until a screen exercises them.
 */
@ExperimentalDataTestingApi
class FakeChargeCommandRepository(
    private val gate: CompletableDeferred<Unit>? = null,
    private val result: () -> MutationResult<Unit> = { MutationResult.Applied(Unit, synced = true) },
) : ChargeCommandRepository {

    /** Every charge created, with the resource it was levied on — a test asserts the payload. */
    val created: MutableList<Triple<String, Int, ChargesPayload>> = mutableListOf()

    override suspend fun createCharges(
        resourceType: String,
        resourceId: Int,
        chargesPayload: ChargesPayload,
    ): MutationResult<Unit> {
        created += Triple(resourceType, resourceId, chargesPayload)
        gate?.await()
        return result()
    }

    override suspend fun deleteCharge(resourceType: String, resourceId: Int, chargeId: Int) =
        unused()

    override suspend fun updateCharge(
        resourceType: String,
        resourceId: Int,
        chargeId: Int,
        payload: ChargesPayload,
    ) = unused()

    private fun unused(): Nothing =
        error("FakeChargeCommandRepository: only createCharges is exercised by a ported screen")
}
