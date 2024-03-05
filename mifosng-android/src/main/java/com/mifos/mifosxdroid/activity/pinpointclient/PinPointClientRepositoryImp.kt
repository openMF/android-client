package com.mifos.mifosxdroid.activity.pinpointclient

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class PinPointClientRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    PinPointClientRepository {

    override fun getClientPinpointLocations(clientId: Int): Observable<List<ClientAddressResponse>> {
        return dataManagerClient.getClientPinpointLocations(clientId)
    }

    override fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest?
    ): Observable<GenericResponse> {
        return dataManagerClient.addClientPinpointLocation(clientId, address)
    }

    override fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int
    ): Observable<GenericResponse> {
        return dataManagerClient.deleteClientAddressPinpointLocation(apptableId, datatableId)
    }

    override fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest?
    ): Observable<GenericResponse> {
        return dataManagerClient.updateClientPinpointLocation(apptableId, datatableId, address)
    }


}