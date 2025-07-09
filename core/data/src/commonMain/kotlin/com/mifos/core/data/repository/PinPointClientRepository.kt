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

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import com.mifos.core.network.model.PinpointLocationActionResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface PinPointClientRepository {

    fun getClientPinpointLocations(clientId: Int): Flow<DataState<List<ClientAddressResponse>>>

    suspend fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest,
    ): PinpointLocationActionResponse

    suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int,
    ): PinpointLocationActionResponse

    suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest,
    ): PinpointLocationActionResponse
}
