/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.activate

import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.network.model.PostCentersCenterIdResponse
import com.mifos.core.network.model.PostClientsClientIdResponse

interface ActivateRepository {

    suspend fun activateClient(
        clientId: Int,
        payload: ActivatePayload,
    ): PostClientsClientIdResponse

    suspend fun activateCenter(
        centerId: Int,
        payload: ActivatePayload,
    ): PostCentersCenterIdResponse

    /**
     * Activates a group. Throws on HTTP failure or transport error — caller wraps in
     * `SubmitHandler.submit { ... }` for structured Submitting / Submitted / Failed lifecycle.
     */
    suspend fun activateGroup(
        groupId: Int,
        payload: ActivatePayload,
    )
}
