package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface PinPointClientRepository {

    suspend fun getClientPinpointLocations(clientId: Int): List<ClientAddressResponse>

    suspend fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest
    ): GenericResponse

    suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int
    ): GenericResponse

    suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest
    ): GenericResponse
}