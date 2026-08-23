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

import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.room.dao.CenterListCacheDao
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterListCacheEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import kpt.core.base.store.paging.PageKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

/**
 * Build the offline-first paged [Store] for the center list, keyed by [PageKey] and
 * streaming a window of the persisted center rows.
 *
 * ### Native Store5 paging idiom (mirror of [provideClientPageStore] / [provideGroupPageStore])
 * ONE offline-first paged stream consumed via
 * `Store<PageKey, List<CenterEntity>>.asPagingScreenStream(...)`:
 *  - **fetcher** — the network page: `getCenters(paged = true, offset = page * pageSize, limit = pageSize)`
 *    returns a `Page<CenterEntity>` whose `.pageItems` are `List<CenterEntity>`. The screen renders
 *    [CenterEntity], so the store's public value type stays `List<CenterEntity>`.
 *  - **source of truth** — a DEDICATED, FK-FREE cache table
 *    ([CenterListCacheEntity], `center_list_cache`) with a `page` column, read/written via
 *    [CenterListCacheDao]. The reader windows the cache by `LIMIT/OFFSET` ([CenterListCacheDao.getPage])
 *    and maps rows back to [CenterEntity] ([toCenterEntity]); the writer clears the page
 *    ([CenterListCacheDao.deleteByPage]) then upserts the fetched page mapped to cache rows
 *    ([toCache]).
 *
 * ### Why a dedicated cache table (the FK-write failure this fixes)
 * The `Center` table ([CenterEntity]) declares a FOREIGN KEY constraint
 * (`Center.id → CenterDate.centerId`). A bulk page upsert has no matching parent `CenterDate`
 * rows, so Room rejects the insert and Store5 surfaces
 * *"Failed to write value to Source of Truth. key: PageKey(page=0…)"*. The `center_list_cache`
 * table has no foreign keys, so a page write can never violate a constraint — exactly like the
 * client-list `client_list_cache` and group-list `group_list_cache` caches.
 *
 * ### Cache lifecycle
 *  - `delete(key)` clears just that page ([CenterListCacheDao.deleteByPage]).
 *  - `deleteAll` wipes the whole cache table ([CenterListCacheDao.deleteAll]) — the logout
 *    cache-clear path driven by `StoreCacheManager.clearAll()`.
 *
 * @param api the network access seam ([DataManagerCenter]) exposing the paged center fetch.
 * @param dao the FK-free paged center-list cache DAO backing the local source-of-truth.
 * @return a [Store] whose key is the page window and whose value is that page's cached centers.
 */
fun provideCenterPageStore(
    api: DataManagerCenter,
    dao: CenterListCacheDao,
): Store<PageKey, List<CenterEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.of { key: PageKey ->
            api.getCenters(
                paged = true,
                offset = key.page * key.pageSize,
                limit = key.pageSize,
            ).pageItems
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: PageKey ->
                dao.getPage(limit = key.pageSize, offset = key.page * key.pageSize)
                    .map { rows -> rows.map { it.toCenterEntity() }.ifEmpty { null } }
            },
            writer = { key: PageKey, centers: List<CenterEntity> ->
                dao.deleteByPage(key.page)
                dao.upsertAll(centers.map { it.toCache(key.page) })
            },
            delete = { key: PageKey -> dao.deleteByPage(key.page) },
            deleteAll = { dao.deleteAll() },
        ),
    )
}

/**
 * Project a fetched [CenterEntity] onto a flat, FK-free [CenterListCacheEntity] for the given
 * [page]. Only the fields the center-list row renders are carried. `CenterEntity.id` is nullable
 * (`Int?`, autoGenerate) — a null id maps to `0` for the cache primary key. `active` is nullable
 * on [CenterEntity]; it is flattened to a non-null `Boolean` (`active == true`), matching the
 * screen's `center.active == true` indicator check.
 */
private fun CenterEntity.toCache(page: Int): CenterListCacheEntity = CenterListCacheEntity(
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
 * Reconstruct the minimal [CenterEntity] the list row needs from a cached [CenterListCacheEntity].
 * Only the rendered fields (`id`, `name`, `accountNo`, `externalId`, `officeName`, `active`,
 * `sync`) are rebuilt; every other [CenterEntity] field (e.g. `status`, `centerDate`, `timeline`)
 * stays at its default — the list row never reads them.
 */
private fun CenterListCacheEntity.toCenterEntity(): CenterEntity = CenterEntity(
    id = id,
    name = name,
    accountNo = accountNo,
    externalId = externalId,
    officeName = officeName,
    active = active,
    sync = sync,
)
