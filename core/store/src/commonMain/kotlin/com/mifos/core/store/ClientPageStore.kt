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

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.dao.ClientDao
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import kpt.core.base.store.paging.PageKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

/**
 * Build the offline-first paged [Store] for the client list, keyed by [PageKey] and
 * streaming a window of the persisted [ClientEntity] rows.
 *
 * ### Native Store5 paging idiom (mirror of the template `CoinMarketsStore`)
 * This replaces the previous dual-path client list (online Paging3 `Pager` +
 * offline `allDatabaseClients` `Flow<Page>`) with ONE offline-first paged stream
 * consumed via `Store<PageKey, List<ClientEntity>>.asPagingScreenStream(...)`:
 *  - **fetcher** — the network page: `getAllClients(offset = page * pageSize, limit = pageSize)`
 *    returns a `Page<ClientEntity>` whose `.pageItems` are the caches value directly (the
 *    screen renders [ClientEntity]; no domain mapping / `toDomain` / `toEntity`).
 *  - **source of truth** — Room windows the `Client` table by `LIMIT/OFFSET`
 *    ([ClientDao.getPageClients]); the writer upserts the fetched page
 *    ([ClientDao.insertClients], REPLACE). `ClientEntity` carries no `page` column, so the
 *    writer cannot delete-by-page — it relies on REPLACE-upsert keyed by the client `id`.
 *
 * ### Cache lifecycle
 *  - `delete(key)` is a no-op — there is no per-page delete (no page column), and
 *    [PagingScreenStream][kpt.core.base.store.paging.PagingScreenStream] never issues a
 *    single-key `clear(key)` (it only reads).
 *  - `deleteAll` wipes the whole `Client` table ([ClientDao.deleteAllClients]) — this is
 *    the logout cache-clear path driven by `StoreCacheManager.clearAll()`.
 *
 * @param api the network access seam ([DataManagerClient]) exposing the paged client fetch.
 * @param dao the Room DAO backing the local paged client source-of-truth.
 * @return a [Store] whose key is the page window and whose value is that page's cached clients.
 */
fun provideClientPageStore(
    api: DataManagerClient,
    dao: ClientDao,
): Store<PageKey, List<ClientEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.of { key: PageKey ->
            api.getAllClients(key.page * key.pageSize, key.pageSize).pageItems
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: PageKey ->
                dao.getPageClients(limit = key.pageSize, offset = key.page * key.pageSize)
                    .map { it.ifEmpty { null } }
            },
            writer = { _: PageKey, clients: List<ClientEntity> ->
                dao.insertClients(clients)
            },
            delete = { _: PageKey -> /* no per-page delete — no page column on ClientEntity */ },
            deleteAll = { dao.deleteAllClients() },
        ),
    )
}
