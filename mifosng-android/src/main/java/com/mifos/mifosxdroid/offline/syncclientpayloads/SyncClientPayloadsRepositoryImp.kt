package com.mifos.mifosxdroid.offline.syncclientpayloads

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import rx.Observable
import javax.inject.Inject

class SyncClientPayloadsRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    SyncClientPayloadsRepository {

    override fun allDatabaseClientPayload(): Observable<List<ClientPayload>> {
        return dataManagerClient.allDatabaseClientPayload
    }

    override fun createClient(clientPayload: ClientPayload): Observable<Client> {
        return dataManagerClient.createClient(clientPayload)
    }

    override fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long
    ): Observable<List<ClientPayload>> {
        return dataManagerClient.deleteAndUpdatePayloads(id, clientCreationTIme)
    }

    override fun updateClientPayload(clientPayload: ClientPayload): Observable<ClientPayload> {
        return dataManagerClient.updateClientPayload(clientPayload)
    }
}