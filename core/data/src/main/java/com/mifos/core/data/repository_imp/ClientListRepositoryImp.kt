package com.mifos.core.data.repository_imp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.data.paging_source.ClientListPagingSource
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import kotlinx.coroutines.flow.Flow
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientListRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient
) : ClientListRepository {

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