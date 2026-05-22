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

import com.mifos.core.data.pathtracking.store.PathTrackingListKey
import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.network.pathtracking.api.PathTrackingApi
import com.mifos.room.pathtracking.dao.PathTrackingCacheDao
import com.mifos.room.pathtracking.mapper.toDomain
import com.mifos.room.pathtracking.mapper.toEntity
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.RetryPolicy
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.executeWithRetry
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import template.core.base.store.infra.StoreFactory

/**
 * Provides the `Store<PathTrackingListKey, List<UserLocation>>` that backs
 * `PathTrackingRepository.pathTrackingStream`.
 *
 * Matches Wave 7 (note)'s `provideNoteListStore` pattern:
 *  - **Fetcher**: calls [PathTrackingApi.getUserPathTracking] with retry-on-reconnect
 *    via cmp-network-monitor's `executeWithRetry`.
 *  - **SourceOfTruth**: reads/writes the `path_tracking_cache` Room table via
 *    [PathTrackingCacheDao]. Writer deletes prior rows for the user before upserting
 *    (lists are refreshed wholesale, not appended). Ordinal preserves on-wire order.
 *
 * Per RULE-STORE5-FETCH-001 — every remote read is wrapped here, so a staff
 * member's tracked-path history is offline-viewable after the first successful
 * fetch + auto-refresh on reconnect.
 */
fun providePathTrackingListStore(
    api: PathTrackingApi,
    networkMonitor: NetworkMonitor,
    dao: PathTrackingCacheDao,
): Store<PathTrackingListKey, List<UserLocation>> = StoreFactory.createStore(
    fetcher = Fetcher.of { key: PathTrackingListKey ->
        networkMonitor.executeWithRetry(
            RetryPolicy { maxAttempts = 1 },
        ) {
            api.getUserPathTracking(key.userId)
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key ->
            dao.observeByUser(key.userId)
                .map { entities -> entities.map { it.toDomain() } }
        },
        writer = { key, locations ->
            dao.deleteByUser(key.userId)
            dao.upsertAll(
                locations.mapIndexed { idx, loc -> loc.toEntity(key.userId, idx) },
            )
        },
        delete = { key -> dao.deleteByUser(key.userId) },
        deleteAll = { dao.deleteAll() },
    ),
)
