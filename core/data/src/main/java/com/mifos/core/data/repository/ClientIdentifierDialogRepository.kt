package com.mifos.core.data.repository

import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface ClientIdentifierDialogRepository {

    suspend fun getClientIdentifierTemplate(clientId: Int): IdentifierTemplate

    suspend fun createClientIdentifier(
        clientId: Int, identifierPayload: IdentifierPayload
    ): IdentifierCreationResponse
}