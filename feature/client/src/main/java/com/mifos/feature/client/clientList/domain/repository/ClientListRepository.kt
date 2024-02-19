package com.mifos.feature.client.clientList.domain.repository

import androidx.paging.PagingData
import com.mifos.core.common.utils.Page
import com.mifos.core.data.model.client.Client
import com.mifos.core.model.ClientDb
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientListRepository {

    fun getAllClients(): Flow<PagingData<Client>>

    fun allDatabaseClients(): Flow<Page<ClientDb>>

}