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
import com.mifos.room.dao.ClientListCacheDao
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientListCacheEntity
import com.mifos.room.entities.client.ClientStatusEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import kpt.core.base.store.paging.PageKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

/**
 * Build the offline-first paged [Store] for the client list, keyed by [PageKey] and
 * streaming a window of the persisted client rows.
 *
 * ### Native Store5 paging idiom (mirror of the template `CoinMarketsStore`)
 * ONE offline-first paged stream consumed via
 * `Store<PageKey, List<ClientEntity>>.asPagingScreenStream(...)`:
 *  - **fetcher** — the network page: `getAllClients(offset = page * pageSize, limit = pageSize)`
 *    returns a `Page<ClientEntity>` whose `.pageItems` are `List<ClientEntity>`. The screen
 *    renders [ClientEntity], so the store's public value type stays `List<ClientEntity>`.
 *  - **source of truth** — a DEDICATED, FK-FREE cache table
 *    ([ClientListCacheEntity], `client_list_cache`) with a `page` column, read/written via
 *    [ClientListCacheDao]. The reader windows the cache by `LIMIT/OFFSET` ([ClientListCacheDao.getPage])
 *    and maps rows back to [ClientEntity] ([toClientEntity]); the writer clears the page
 *    ([ClientListCacheDao.deleteByPage]) then upserts the fetched page mapped to cache rows
 *    ([toCache]).
 *
 * ### Why a dedicated cache table (the FK-write failure this fixes)
 * The previous SoT persisted into the `Client` table ([ClientEntity]), which declares two
 * FOREIGN KEY constraints (`Client.id → Status.id`, `Client.id → ClientDate.clientId`). A bulk
 * page upsert has no matching parent `Status` / `ClientDate` rows, so Room rejects the insert
 * and Store5 surfaces *"Failed to write value to Source of Truth. key: PageKey(page=0…)"*.
 * The `client_list_cache` table has no foreign keys, so a page write can never violate a
 * constraint — exactly like the template crypto `coin_markets` cache.
 *
 * ### Cache lifecycle
 *  - `delete(key)` clears just that page ([ClientListCacheDao.deleteByPage]).
 *  - `deleteAll` wipes the whole cache table ([ClientListCacheDao.deleteAll]) — the logout
 *    cache-clear path driven by `StoreCacheManager.clearAll()`.
 *
 * @param api the network access seam ([DataManagerClient]) exposing the paged client fetch.
 * @param dao the FK-free paged client-list cache DAO backing the local source-of-truth.
 * @return a [Store] whose key is the page window and whose value is that page's cached clients.
 */
fun provideClientPageStore(
    api: DataManagerClient,
    dao: ClientListCacheDao,
): Store<PageKey, List<ClientEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.of { key: PageKey ->
            api.getAllClients(key.page * key.pageSize, key.pageSize).pageItems
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: PageKey ->
                dao.getPage(limit = key.pageSize, offset = key.page * key.pageSize)
                    .map { rows -> rows.map { it.toClientEntity() }.ifEmpty { null } }
            },
            writer = { key: PageKey, clients: List<ClientEntity> ->
                dao.deleteByPage(key.page)
                dao.upsertAll(clients.map { it.toCache(key.page) })
            },
            delete = { key: PageKey -> dao.deleteByPage(key.page) },
            deleteAll = { dao.deleteAll() },
        ),
    )
}

/**
 * Project a fetched [ClientEntity] onto a flat, FK-free [ClientListCacheEntity] for the given
 * [page]. Only the 6 fields the client-list row renders are carried; the nested status is
 * flattened to `statusCode` / `statusValue`.
 */
private fun ClientEntity.toCache(page: Int): ClientListCacheEntity = ClientListCacheEntity(
    id = id,
    page = page,
    accountNo = accountNo,
    displayName = displayName,
    externalId = externalId,
    officeName = officeName,
    statusCode = status?.code,
    statusValue = status?.value,
    active = active,
    sync = sync,
)

/**
 * Reconstruct the minimal [ClientEntity] the list row needs from a cached [ClientListCacheEntity].
 * Rebuilds the nested [ClientStatusEntity] from the flattened `statusCode` / `statusValue`;
 * FK-relevant / unrendered fields (e.g. `clientDate`) stay at their [ClientEntity] defaults —
 * the list row only reads `id`, `accountNo`, `displayName`, `externalId`, `officeName`, and
 * `status.value`.
 */
private fun ClientListCacheEntity.toClientEntity(): ClientEntity = ClientEntity(
    id = id,
    accountNo = accountNo,
    displayName = displayName,
    externalId = externalId,
    officeName = officeName,
    status = ClientStatusEntity(code = statusCode, value = statusValue),
    active = active,
    sync = sync,
)
