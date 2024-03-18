package com.mifos.mifosxdroid.online.clientidentifiers

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.noncore.Identifier
import org.apache.fineract.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
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
    ): Observable<DeleteClientsClientIdIdentifiersIdentifierIdResponse> {
        return dataManagerClient.deleteClientIdentifier(clientId, identifierId)
    }


}