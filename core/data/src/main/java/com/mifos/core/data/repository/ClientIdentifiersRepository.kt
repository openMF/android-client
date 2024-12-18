package com.mifos.core.data.repository

import com.mifos.core.objects.noncore.Identifier
import org.openapitools.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientIdentifiersRepository {

    suspend fun getClientIdentifiers(clientId: Int): List<Identifier>

    suspend fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int
    ): DeleteClientsClientIdIdentifiersIdentifierIdResponse
}