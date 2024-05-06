package com.mifos.feature.client.clientList.domain.repository

import androidx.paging.PagingData
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import kotlinx.coroutines.flow.Flow
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientListRepository {

    fun getAllClients(): Flow<PagingData<Client>>

    fun allDatabaseClients(): Observable<Page<Client>>

}