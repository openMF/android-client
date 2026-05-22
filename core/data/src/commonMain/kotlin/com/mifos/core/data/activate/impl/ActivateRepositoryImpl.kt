/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.activate.impl

import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.data.activate.ActivateRepository
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.network.center.api.CenterApi
import com.mifos.core.network.client.api.ClientApi
import com.mifos.core.network.group.api.GroupApi
import com.mifos.core.model.network.PostCentersCenterIdResponse
import com.mifos.core.model.network.PostClientsClientIdResponse

class ActivateRepositoryImpl(
    private val clientApi: ClientApi,
    private val centerApi: CenterApi,
    private val groupApi: GroupApi,
) : ActivateRepository {

    override suspend fun activateClient(
        clientId: Int,
        payload: ActivatePayload,
    ): PostClientsClientIdResponse = clientApi.activate(clientId, payload)

    override suspend fun activateCenter(
        centerId: Int,
        payload: ActivatePayload,
    ): PostCentersCenterIdResponse = centerApi.activate(centerId, payload)

    override suspend fun activateGroup(
        groupId: Int,
        payload: ActivatePayload,
    ) {
        val response = groupApi.activate(groupId, payload)
        if (response.status.value !in 200..299) {
            throw Exception(extractErrorMessage(response))
        }
    }
}
