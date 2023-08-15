package com.mifos.repositories

import com.mifos.objects.noncore.IdentifierCreationResponse
import com.mifos.objects.noncore.IdentifierPayload
import com.mifos.objects.noncore.IdentifierTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface IdentifierDialogRepository {

    fun getClientIdentifierTemplate(clientId: Int): Observable<IdentifierTemplate>

    fun createClientIdentifier(
        clientId: Int, identifierPayload: IdentifierPayload?
    ): Observable<IdentifierCreationResponse>
}