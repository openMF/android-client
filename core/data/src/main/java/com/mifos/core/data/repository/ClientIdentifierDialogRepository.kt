package com.mifos.core.data.repository

import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface ClientIdentifierDialogRepository {

    fun getClientIdentifierTemplate(clientId: Int): Observable<IdentifierTemplate>

    suspend fun createClientIdentifier(
        clientId: Int, identifierPayload: IdentifierPayload
    ): IdentifierCreationResponse
}