package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.objects.client.Client
import com.mifos.objects.client.Page
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientListRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) : ClientListRepository {

    override fun getAllClients(paged: Boolean, offset: Int, limit: Int): Observable<Page<Client>> {
        return dataManagerClient.getAllClients(paged,offset,limit)
    }

    override fun allDatabaseClients(): Observable<Page<Client>> {
        return dataManagerClient.allDatabaseClients
    }
}