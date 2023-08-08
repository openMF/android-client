package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.objects.client.ClientAddressRequest
import com.mifos.objects.client.ClientAddressResponse
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