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
import com.mifos.room.dao.GroupListCacheDao
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupListCacheEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import kpt.core.base.store.paging.PageKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

/**
 * Build the offline-first paged [Store] for the group list, keyed by [PageKey] and
 * streaming a window of the persisted group rows.
 *
 * ### Native Store5 paging idiom (mirror of [provideClientPageStore])
 * ONE offline-first paged stream consumed via
 * `Store<PageKey, List<GroupEntity>>.asPagingScreenStream(...)`:
 *  - **fetcher** — the network page: `getGroups(paged = true, offset = page * pageSize, limit = pageSize)`
 *    returns a `Page<GroupEntity>` whose `.pageItems` are `List<GroupEntity>`. The screen renders
 *    [GroupEntity], so the store's public value type stays `List<GroupEntity>`.
 *  - **source of truth** — a DEDICATED, FK-FREE cache table
 *    ([GroupListCacheEntity], `group_list_cache`) with a `page` column, read/written via
 *    [GroupListCacheDao]. The reader windows the cache by `LIMIT/OFFSET` ([GroupListCacheDao.getPage])
 *    and maps rows back to [GroupEntity] ([toGroupEntity]); the writer clears the page
 *    ([GroupListCacheDao.deleteByPage]) then upserts the fetched page mapped to cache rows
 *    ([toCache]).
 *
 * ### Why a dedicated cache table (the FK-write failure this fixes)
 * The previous SoT persisted into the `GroupTable` ([GroupEntity]), which declares a FOREIGN KEY
 * constraint (`GroupTable.groupDate → GroupDate.groupId`). A bulk page upsert has no matching
 * parent `GroupDate` rows, so Room rejects the insert and Store5 surfaces
 * *"Failed to write value to Source of Truth. key: PageKey(page=0…)"*. The `group_list_cache`
 * table has no foreign keys, so a page write can never violate a constraint — exactly like the
 * client-list `client_list_cache` cache.
 *
 * ### Cache lifecycle
 *  - `delete(key)` clears just that page ([GroupListCacheDao.deleteByPage]).
 *  - `deleteAll` wipes the whole cache table ([GroupListCacheDao.deleteAll]) — the logout
 *    cache-clear path driven by `StoreCacheManager.clearAll()`.
 *
 * @param api the network access seam ([DataManagerGroups]) exposing the paged group fetch.
 * @param dao the FK-free paged group-list cache DAO backing the local source-of-truth.
 * @return a [Store] whose key is the page window and whose value is that page's cached groups.
 */
fun provideGroupPageStore(
    api: DataManagerGroups,
    dao: GroupListCacheDao,
): Store<PageKey, List<GroupEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.of { key: PageKey ->
            api.getGroups(
                paged = true,
                offset = key.page * key.pageSize,
                limit = key.pageSize,
            ).pageItems
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: PageKey ->
                dao.getPage(limit = key.pageSize, offset = key.page * key.pageSize)
                    .map { rows -> rows.map { it.toGroupEntity() }.ifEmpty { null } }
            },
            writer = { key: PageKey, groups: List<GroupEntity> ->
                dao.deleteByPage(key.page)
                dao.upsertAll(groups.map { it.toCache(key.page) })
            },
            delete = { key: PageKey -> dao.deleteByPage(key.page) },
            deleteAll = { dao.deleteAll() },
        ),
    )
}

/**
 * Project a fetched [GroupEntity] onto a flat, FK-free [GroupListCacheEntity] for the given
 * [page]. Only the fields the group-list row renders are carried. `GroupEntity.id` is nullable
 * (`Int?`, autoGenerate) — a null id maps to `0` for the cache primary key. `active` is nullable
 * on [GroupEntity]; it is flattened to a non-null `Boolean` (`active == true`), matching the
 * screen's `group.active == true` indicator check.
 */
private fun GroupEntity.toCache(page: Int): GroupListCacheEntity = GroupListCacheEntity(
    id = id ?: 0,
    page = page,
    name = name,
    accountNo = accountNo,
    externalId = externalId,
    officeName = officeName,
    active = active == true,
    sync = sync,
)

/**
 * Reconstruct the minimal [GroupEntity] the list row needs from a cached [GroupListCacheEntity].
 * Only the rendered fields (`id`, `name`, `accountNo`, `externalId`, `officeName`, `active`,
 * `sync`) are rebuilt; every other [GroupEntity] field (e.g. `status`, `groupDate`, `timeline`)
 * stays at its default — the list row never reads them.
 */
private fun GroupListCacheEntity.toGroupEntity(): GroupEntity = GroupEntity(
    id = id,
    name = name,
    accountNo = accountNo,
    externalId = externalId,
    officeName = officeName,
    active = active,
    sync = sync,
)
