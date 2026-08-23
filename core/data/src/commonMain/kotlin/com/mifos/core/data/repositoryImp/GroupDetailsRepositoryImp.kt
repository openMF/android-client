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

import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupWithAssociations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Created by Aditya Gupta on 06/08/23.
 *
 * Offline-first read path: [getGroup] streams the Store5 group store (qualifier
 * [AppStoreRegistry.Groups][kpt.core.store.AppStoreRegistry.Groups] /
 * [provideGroupStore][com.mifos.core.store.provideGroupStore]) instead of the raw
 * `DataManagerGroups.getGroup` network call. `StoreReadRequest.cached(refresh = true)` emits the
 * Room-persisted [GroupEntity] immediately (renders offline from cache) AND triggers a
 * background network refresh (SWR). The fetcher's network error is intentionally swallowed —
 * the source-of-truth reader still emits the cached row, so the screen shows cached data offline
 * rather than an "Unable to resolve host" error. The other two methods stay on the existing
 * `DataManagerGroups` path unchanged.
 */
class GroupDetailsRepositoryImp(
    private val groupStore: Store<Int, GroupEntity>,
    private val dataManagerGroups: DataManagerGroups,
) : GroupDetailsRepository {

    override fun getGroup(groupId: Int): Flow<GroupEntity> {
        return groupStore
            .stream(StoreReadRequest.cached(key = groupId, refresh = true))
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value
                    // Offline-first: fetch error is non-fatal. The SoT reader emits the cached
                    // group as a separate Data response, so we never surface a network error.
                    else -> null
                }
            }
    }

    override fun getGroupAccounts(groupId: Int): Flow<GroupAccounts> {
        return dataManagerGroups.getGroupAccounts(groupId)
            
    }

    override fun getGroupWithAssociations(groupId: Int): Flow<GroupWithAssociations> {
        return dataManagerGroups.getGroupWithAssociations(groupId)
            
    }
}
