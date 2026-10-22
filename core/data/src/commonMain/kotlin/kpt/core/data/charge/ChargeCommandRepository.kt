/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.charge

import kpt.core.base.store.mutation.MutationResult
import kpt.core.model.objects.payloads.ChargesPayload

/**
 * Every charge write, as an explicit outcome.
 *
 * Each returns [MutationResult] rather than `Unit`: a field officer has to be told whether the
 * change reached the server, was refused because the device is offline, or failed — a distinction
 * that cannot be reconstructed above this layer.
 *
 * These are commands, so they carry no Store and no cache. Offline CAPTURE is a different
 * operation with its own explicit queue ([kpt.core.data.sync.OfflineQueueRepository]); it is never
 * a silent fallback from here.
 */
interface ChargeCommandRepository {

    /**
     * Levy a charge on a client or a loan.
     *
     * `Unit`, not `HttpResponse`: the endpoint's Ktorfit signature returns a raw response, and a
     * FEATURE cannot name a Ktor type — `feature/loan` must not depend on `core:network`. The
     * gateway already turns a non-2xx into [MutationResult.Failed], which is the only distinction
     * a screen renders. Same change as the seven client commands and the loan transfer.
     */
    suspend fun createCharges(
        resourceType: String,
        resourceId: Int,
        chargesPayload: ChargesPayload,
    ): MutationResult<Unit>

    suspend fun deleteCharge(resourceType: String, resourceId: Int, chargeId: Int): MutationResult<Unit>

    suspend fun updateCharge(resourceType: String, resourceId: Int, chargeId: Int, payload: ChargesPayload): MutationResult<Unit>
}
