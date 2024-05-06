package com.mifos.feature.client.clientList.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import com.mifos.feature.client.clientList.domain.repository.ClientListRepository
import com.mifos.feature.client.clientList.paging.ClientListPagingSource
import kotlinx.coroutines.flow.Flow
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientListRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientListRepository {

    override fun getAllClients(): Flow<PagingData<Client>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ), pagingSourceFactory = {
                ClientListPagingSource(dataManagerClient)
            }
        ).flow
    }

    override fun allDatabaseClients(): Observable<Page<Client>> {
        return dataManagerClient.allDatabaseClients
    }
}