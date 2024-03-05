package com.mifos.mifosxdroid.online.clientidentifiers

import com.mifos.core.objects.noncore.Identifier
import org.apache.fineract.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientIdentifiersRepository {

    fun getClientIdentifiers(clientId: Int): Observable<List<Identifier>>

    fun deleteClientIdentifier(
        clientId: Int,
        identifierId: Int
    ): Observable<DeleteClientsClientIdIdentifiersIdentifierIdResponse>
}