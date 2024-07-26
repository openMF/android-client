package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
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
        address: ClientAddressRequest
    ): GenericResponse {
        return dataManagerClient.addClientPinpointLocation(clientId, address)
    }

    override suspend fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int
    ): GenericResponse {
        return dataManagerClient.deleteClientAddressPinpointLocation(apptableId, datatableId)
    }

    override suspend fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest
    ): GenericResponse {
        return dataManagerClient.updateClientPinpointLocation(apptableId, datatableId, address)
    }
}