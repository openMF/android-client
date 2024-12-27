/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.modelobjects.clients.ClientAddressRequest
import com.mifos.core.modelobjects.clients.ClientAddressResponse
import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface PinPointClientRepository {

    suspend fun getClientPinpointLocations(clientId: Int): List<ClientAddressResponse>

    suspend fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest,
    ): GenericResponse

    suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int,
    ): GenericResponse

    suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest,
    ): GenericResponse
}
