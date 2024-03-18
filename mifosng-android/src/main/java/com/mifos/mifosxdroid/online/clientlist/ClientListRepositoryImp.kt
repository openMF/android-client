package com.mifos.mifosxdroid.online.clientlist

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientListRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientListRepository {

    override fun getAllClients(offset: Int, limit: Int): Observable<Page<Client>> {
        return dataManagerClient.getAllClients(offset, limit)
    }

    override fun allDatabaseClients(): Observable<Page<Client>> {
        return dataManagerClient.allDatabaseClients
    }
}