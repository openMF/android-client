package com.mifos.feature.client.clientList.domain.repository

import androidx.paging.PagingData
import com.mifos.core.model.ClientDb
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientListRepository {

    fun getAllClients(): Flow<PagingData<Client>>

    fun allDatabaseClients(): Flow<Page<ClientDb>>

}