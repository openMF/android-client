/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.model.PinpointLocationActionResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class PinPointClientRepositoryImp(
    private val dataManagerClient: DataManagerClient,
) : PinPointClientRepository {

    override fun getClientPinpointLocations(clientId: Int): Flow<DataState<List<ClientAddressResponse>>> {
        return dataManagerClient.getClientPinpointLocations(clientId)
            .asDataStateFlow()
    }

    override suspend fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest,
    ): PinpointLocationActionResponse {
        return dataManagerClient.addClientPinpointLocation(clientId, address)
    }

    override suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int,
    ): PinpointLocationActionResponse {
        return dataManagerClient.deleteClientAddressPinpointLocation(apptableId, datatableId)
    }

    override suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest,
    ): PinpointLocationActionResponse {
        return dataManagerClient.updateClientPinpointLocation(apptableId, datatableId, address)
    }
}
