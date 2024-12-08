package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.ClientIdentifierDialogRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class ClientIdentifierDialogRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientIdentifierDialogRepository {

    override suspend fun getClientIdentifierTemplate(clientId: Int): IdentifierTemplate {
        return dataManagerClient.getClientIdentifierTemplate(clientId)
    }

    override suspend fun createClientIdentifier(
        clientId: Int,
        identifierPayload: IdentifierPayload
    ): IdentifierCreationResponse {
        return dataManagerClient.createClientIdentifier(clientId, identifierPayload)
    }
}