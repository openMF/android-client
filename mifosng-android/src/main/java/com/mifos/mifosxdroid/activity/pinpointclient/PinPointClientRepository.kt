package com.mifos.mifosxdroid.activity.pinpointclient

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface PinPointClientRepository {

    fun getClientPinpointLocations(clientId: Int): Observable<List<ClientAddressResponse>>

    fun addClientPinpointLocation(
        clientId: Int,
        address: ClientAddressRequest?
    ): Observable<GenericResponse>

    fun deleteClientAddressPinpointLocation(
        apptableId: Int,
        datatableId: Int
    ): Observable<GenericResponse>

    fun updateClientPinpointLocation(
        apptableId: Int,
        datatableId: Int,
        address: ClientAddressRequest?
    ): Observable<GenericResponse>
}