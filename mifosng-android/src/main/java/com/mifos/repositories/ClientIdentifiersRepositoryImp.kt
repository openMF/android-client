package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.objects.noncore.Identifier
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientIdentifiersRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientIdentifiersRepository {

    override fun getClientIdentifiers(clientId: Int): Observable<List<Identifier>> {
        return dataManagerClient.getClientIdentifiers(clientId)
    }

    override fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int
    ): Observable<GenericResponse> {
        return dataManagerClient.deleteClientIdentifier(clientId, identifierId)
    }


}