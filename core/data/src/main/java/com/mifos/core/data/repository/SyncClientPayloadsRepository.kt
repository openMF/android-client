package com.mifos.core.data.repository

import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import rx.Observable

interface SyncClientPayloadsRepository {

    fun allDatabaseClientPayload(): Observable<List<ClientPayload>>

    fun createClient(clientPayload: ClientPayload): Observable<Client>

    fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long
    ): Observable<List<ClientPayload>>

    fun updateClientPayload(clientPayload: ClientPayload): Observable<ClientPayload>
}