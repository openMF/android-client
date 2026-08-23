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
import com.mifos.core.data.pagingSource.CenterListPagingSource
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kpt.core.base.data.infra.NetworkMonitor
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.paging.PageKey
import kpt.core.base.store.paging.PagingScreenStream
import kpt.core.base.store.paging.asPagingScreenStream
import org.mobilenativefoundation.store.store5.Store

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterListRepositoryImp(
    private val centerListPageStore: Store<PageKey, List<CenterEntity>>,
    private val networkMonitor: NetworkMonitor,
    private val fetchedAtRepository: FetchedAtRepository,
    private val dataManagerCenter: DataManagerCenter,
) : CenterListRepository {

    override fun centerListPagingStream(
        scope: CoroutineScope,
        pageSize: Int,
    ): PagingScreenStream<CenterEntity> = centerListPageStore.asPagingScreenStream(
        networkMonitor = networkMonitor,
        fetchedAtRepository = fetchedAtRepository,
        cacheKey = "center:list",
        scope = scope,
        pageSize = pageSize,
    )

    override fun getAllCenters(): Flow<PagingData<CenterEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                CenterListPagingSource(dataManagerCenter)
            },
        ).flow
    }

    override suspend fun getCentersGroupAndMeeting(id: Int): CenterWithAssociations {
        return dataManagerCenter.getCentersGroupAndMeeting(id)
    }

    override fun allDatabaseCenters(): Flow<Page<CenterEntity>> {
        return dataManagerCenter.allDatabaseCenters
    }
}
