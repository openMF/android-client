package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.noncore.Identifier
import org.openapitools.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientIdentifiersRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientIdentifiersRepository {

    override suspend fun getClientIdentifiers(clientId: Int): List<Identifier> {
        return dataManagerClient.getClientIdentifiers(clientId)
    }

    override suspend fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int
    ): DeleteClientsClientIdIdentifiersIdentifierIdResponse {
        return dataManagerClient.deleteClientIdentifier(clientId, identifierId)
    }

}