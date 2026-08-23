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

import com.mifos.core.common.utils.Page
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.room.entities.group.GroupEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kpt.core.base.data.infra.NetworkMonitor
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.paging.PageKey
import kpt.core.base.store.paging.PagingScreenStream
import kpt.core.base.store.paging.asPagingScreenStream
import org.mobilenativefoundation.store.store5.Store

class GroupsListRepositoryImpl(
    private val groupListPageStore: Store<PageKey, List<GroupEntity>>,
    private val networkMonitor: NetworkMonitor,
    private val fetchedAtRepository: FetchedAtRepository,
    private val dataManager: DataManagerGroups,
) : GroupsListRepository {

    override fun groupListPagingStream(
        scope: CoroutineScope,
        pageSize: Int,
    ): PagingScreenStream<GroupEntity> = groupListPageStore.asPagingScreenStream(
        networkMonitor = networkMonitor,
        fetchedAtRepository = fetchedAtRepository,
        cacheKey = "group:list",
        scope = scope,
        pageSize = pageSize,
    )

    override suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<GroupEntity> =
        dataManager.getGroups(paged, offset, limit).pageItems

    override fun getAllLocalGroups(): Flow<Page<GroupEntity>> {
        return dataManager.databaseGroups
    }
}
