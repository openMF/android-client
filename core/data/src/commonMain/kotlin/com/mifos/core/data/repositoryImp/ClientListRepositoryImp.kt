/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.common.utils.Page
import com.mifos.core.data.pagingSource.ClientListPagingSource
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kpt.core.base.data.infra.NetworkMonitor
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.paging.PageKey
import kpt.core.base.store.paging.PagingScreenStream
import kpt.core.base.store.paging.asPagingScreenStream
import org.mobilenativefoundation.store.store5.Store

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientListRepositoryImp(
    private val clientListPageStore: Store<PageKey, List<ClientEntity>>,
    private val networkMonitor: NetworkMonitor,
    private val fetchedAtRepository: FetchedAtRepository,
    private val dataManagerClient: DataManagerClient,
) : ClientListRepository {

    override fun clientListPagingStream(
        scope: CoroutineScope,
        pageSize: Int,
    ): PagingScreenStream<ClientEntity> = clientListPageStore.asPagingScreenStream(
        networkMonitor = networkMonitor,
        fetchedAtRepository = fetchedAtRepository,
        cacheKey = "client:list",
        scope = scope,
        pageSize = pageSize,
    )

    override fun getAllClients(): Flow<PagingData<ClientEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                ClientListPagingSource(dataManagerClient)
            },
        ).flow
    }

    override fun allDatabaseClients(): Flow<Page<ClientEntity>> {
        return dataManagerClient.allDatabaseClients
    }
}
