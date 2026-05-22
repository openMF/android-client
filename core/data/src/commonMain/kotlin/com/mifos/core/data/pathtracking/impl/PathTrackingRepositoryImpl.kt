/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.pathtracking.impl

import com.mifos.core.data.pathtracking.PathTrackingRepository
import com.mifos.core.data.pathtracking.store.PathTrackingListKey
import com.mifos.core.data.pathtracking.store.cacheKey
import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.network.pathtracking.api.PathTrackingApi
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import template.core.base.store.infra.FetchedAtRepository
import template.core.base.store.screen.ScreenDataStream
import template.core.base.store.screen.asScreenStream

/**
 * Default [PathTrackingRepository] backed by:
 *  - [pathTrackingApi] for direct writes (mutations).
 *  - [pathTrackingListStore] for the offline-first list read (Store5; see
 *    [providePathTrackingListStore]).
 *
 * Mutations propagate exceptions to the caller. After a successful append, the
 * impl calls `store.fresh(key)` so the cache + subscribers see the new entry
 * automatically.
 */
class PathTrackingRepositoryImpl(
    private val pathTrackingApi: PathTrackingApi,
    private val pathTrackingListStore: Store<PathTrackingListKey, List<UserLocation>>,
    private val networkMonitor: NetworkMonitor,
    private val fetchedAtRepository: FetchedAtRepository,
) : PathTrackingRepository {

    override fun pathTrackingStream(
        keyFlow: Flow<PathTrackingListKey>,
        scope: CoroutineScope,
    ): ScreenDataStream<List<UserLocation>> = pathTrackingListStore.asScreenStream(
        keyFlow = keyFlow,
        networkMonitor = networkMonitor,
        fetchedAtRepository = fetchedAtRepository,
        cacheKeyFor = { it.cacheKey() },
        scope = scope,
    )

    override suspend fun addUserPathTracking(
        userId: Int,
        userLocation: UserLocation,
    ) {
        pathTrackingApi.addUserPathTracking(userId, userLocation)
        invalidateList(userId)
    }

    /**
     * Force the Store5 cache to refetch from network after a successful write.
     * Stream subscribers receive the new list automatically via SourceOfTruth.
     */
    private suspend fun invalidateList(userId: Int) {
        val key = PathTrackingListKey(userId)
        pathTrackingListStore.stream(StoreReadRequest.fresh(key))
            .first { response ->
                response is StoreReadResponse.Data<*> || response is StoreReadResponse.Error<*>
            }
    }
}
