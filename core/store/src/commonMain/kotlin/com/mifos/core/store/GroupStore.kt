/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.store

import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.room.dao.GroupsDao
import com.mifos.room.entities.group.GroupEntity
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration.Companion.minutes

/**
 * Build the read-only offline-first [Store] for a single group's details, keyed by `groupId`
 * and streaming the persisted [GroupEntity] row.
 *
 * ### Read path (mirror of [provideLoanTransactionStore])
 * The group-details screen must render from the local cache when the officer is offline. This
 * Store fronts [DataManagerGroups.getGroup] (the SAME single-group fetch the previous
 * network-first [GroupDetailsRepositoryImp][com.mifos.core.data.repositoryImp.GroupDetailsRepositoryImp]
 * used) with a Room source-of-truth so `StoreReadRequest.cached(refresh = true)` emits the
 * persisted [GroupEntity] immediately AND kicks a background refresh (stale-while-revalidate).
 * The fetch error is intentionally non-fatal at the consumer — the SoT reader still emits the
 * cached row so the screen shows cached data offline rather than an "Unable to resolve host"
 * error.
 *
 * ### Fetcher shape
 * [DataManagerGroups.getGroup] already returns a cold `Flow<GroupEntity>`, so [Fetcher.ofFlow]
 * composes it straight through (no `.first()`-bridged [Fetcher.of] needed) — matching how
 * `StoreFactory` builds Flow-backed fetchers. `Input == Output == GroupEntity`; no mapping
 * layer is required because the network fetch and the Room row are the same entity type.
 *
 * ### Source of truth
 * reader = [GroupsDao.getGroupById] (non-null `Flow<GroupEntity>`, used directly). writer =
 * [GroupsDao.insertGroup] (REPLACE conflict — upsert of the single row). delete /deleteAll =
 * [GroupsDao.deleteGroup] / [GroupsDao.deleteAllGroups] so `Store.clear()` wipes the GroupTable
 * on logout via `StoreCacheManager` (D7).
 *
 * @param api the network access seam ([DataManagerGroups]) exposing the single-group fetch.
 * @param dao the Room DAO backing the local group source-of-truth.
 * @return a [Store] whose key is the group id and whose value is the cached group.
 */
fun provideGroupStore(
    api: DataManagerGroups,
    dao: GroupsDao,
): Store<Int, GroupEntity> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { groupId: Int ->
            api.getGroup(groupId)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { groupId: Int -> dao.getGroupById(groupId) },
            writer = { _: Int, group: GroupEntity -> dao.insertGroup(group) },
            delete = { groupId: Int -> dao.deleteGroup(groupId) },
            deleteAll = { dao.deleteAllGroups() },
        ),
        // Group details are mutable (status, staff, meeting date can change) — expire the
        // in-memory copy after a short window so a revisit re-reads Room / refreshes network.
        memoryPolicy = MemoryPolicy.builder<Int, GroupEntity>()
            .setExpireAfterWrite(5.minutes)
            .build(),
    )
}
