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

import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.`object`.clients.ClientAddressRequest
import com.mifos.core.`object`.clients.ClientAddressResponse
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerClient
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class PinPointClientRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    PinPointClientRepository {

    override suspend fun getClientPinpointLocations(clientId: Int): List<ClientAddressResponse> {
        return dataManagerClient.getClientPinpointLocations(clientId)
    }

    override suspend fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest,
    ): GenericResponse {
        return dataManagerClient.addClientPinpointLocation(clientId, address)
    }

    override suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int,
    ): GenericResponse {
        return dataManagerClient.deleteClientAddressPinpointLocation(apptableId, datatableId)
    }

    override suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest,
    ): GenericResponse {
        return dataManagerClient.updateClientPinpointLocation(apptableId, datatableId, address)
    }
}
