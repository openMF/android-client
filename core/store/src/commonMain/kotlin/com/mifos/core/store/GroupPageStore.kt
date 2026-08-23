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
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import kpt.core.base.store.paging.PageKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

/**
 * Build the offline-first paged [Store] for the group list, keyed by [PageKey] and
 * streaming a window of the persisted [GroupEntity] rows.
 *
 * ### Native Store5 paging idiom (mirror of [provideClientPageStore])
 * This replaces the previous online Paging3 `Pager` (`GroupsListPagingDataSource`) driving the
 * group list with ONE offline-first paged stream consumed via
 * `Store<PageKey, List<GroupEntity>>.asPagingScreenStream(...)`:
 *  - **fetcher** — the network page: `getGroups(paged = true, offset = page * pageSize, limit = pageSize)`
 *    returns a `Page<GroupEntity>` whose `.pageItems` are the cached value directly (the screen
 *    renders [GroupEntity]; no domain mapping / `toDomain` / `toEntity`).
 *  - **source of truth** — Room windows the `GroupTable` by `LIMIT/OFFSET`
 *    ([GroupsDao.getPageGroups]); the writer upserts the fetched page
 *    ([GroupsDao.insertGroups], REPLACE). `GroupEntity` carries no `page` column, so the writer
 *    cannot delete-by-page — it relies on REPLACE-upsert keyed by the group `id`.
 *
 * ### Cache lifecycle
 *  - `delete(key)` is a no-op — there is no per-page delete (no page column), and
 *    [PagingScreenStream][kpt.core.base.store.paging.PagingScreenStream] never issues a
 *    single-key `clear(key)` (it only reads).
 *  - `deleteAll` wipes the whole `GroupTable` ([GroupsDao.deleteAllGroups]) — this is the
 *    logout cache-clear path driven by `StoreCacheManager.clearAll()`.
 *
 * @param api the network access seam ([DataManagerGroups]) exposing the paged group fetch.
 * @param dao the Room DAO backing the local paged group source-of-truth.
 * @return a [Store] whose key is the page window and whose value is that page's cached groups.
 */
fun provideGroupPageStore(
    api: DataManagerGroups,
    dao: GroupsDao,
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
                dao.getPageGroups(limit = key.pageSize, offset = key.page * key.pageSize)
                    .map { it.ifEmpty { null } }
            },
            writer = { _: PageKey, groups: List<GroupEntity> ->
                dao.insertGroups(groups)
            },
            delete = { _: PageKey -> /* no per-page delete — no page column on GroupEntity */ },
            deleteAll = { dao.deleteAllGroups() },
        ),
    )
}
