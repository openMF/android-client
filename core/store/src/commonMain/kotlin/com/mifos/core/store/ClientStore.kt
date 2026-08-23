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
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration.Companion.minutes

/**
 * Build the read-only offline-first [Store] for a single client's details, keyed by `clientId`
 * and streaming the persisted [ClientEntity] row.
 *
 * ### Read path (mirror of [provideGroupStore] / [provideLoanTransactionStore])
 * The client-details screen must render from the local cache when the officer is offline. This
 * Store fronts [DataManagerClient.getClient] (the SAME single-client fetch the previous
 * network-first [ClientDetailsRepositoryImp][com.mifos.core.data.repositoryImp.ClientDetailsRepositoryImp]
 * used) with a Room source-of-truth so `StoreReadRequest.cached(refresh = true)` emits the
 * persisted [ClientEntity] immediately AND kicks a background refresh (stale-while-revalidate).
 * The fetch error is intentionally non-fatal at the consumer — the SoT reader still emits the
 * cached row so the screen shows cached data offline rather than an "Unable to resolve host"
 * error.
 *
 * ### Fetcher shape
 * Unlike [provideGroupStore] (whose `getGroup` already returns a cold `Flow`),
 * [DataManagerClient.getClient] is a plain `suspend fun ... : ClientEntity`, so [Fetcher.of]
 * (the suspend-lambda variant) is used to bridge it. The fetcher also carries the
 * group-name back-fill that previously lived in `ClientDetailsRepositoryImp.getClient` — see
 * [resolveGroup] — so the ENRICHED entity is what gets persisted to Room (the fix-up now
 * survives offline instead of being re-derived on every network read). `Input == Output ==
 * ClientEntity`; no mapping layer is required.
 *
 * ### Source of truth
 * reader = [ClientDao.getClientByClientId] — a NULLABLE `Flow<ClientEntity?>`, used directly:
 * Store5's [SourceOfTruth.of] reader is typed `Flow<Local?>`, so a `null` emission means
 * "SoT empty" (triggers the fetcher) and a non-null emission is served straight through. writer =
 * [ClientDao.insertClient] (REPLACE conflict — upsert of the single row). No `delete`/`deleteAll`
 * is wired because [ClientDao] exposes no Client-table delete query; logout cache clearing for
 * this store is a no-op at the SoT layer (see residual risks in the registration manifest).
 *
 * @param api the network access seam ([DataManagerClient]) exposing the single-client fetch.
 * @param dao the Room DAO backing the local client source-of-truth.
 * @return a [Store] whose key is the client id and whose value is the cached client.
 */
fun provideClientStore(
    api: DataManagerClient,
    dao: ClientDao,
): Store<Int, ClientEntity> {
    return StoreFactory.createStore(
        fetcher = Fetcher.of { clientId: Int ->
            api.getClient(clientId).resolveGroup()
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { clientId: Int -> dao.getClientByClientId(clientId) },
            writer = { _: Int, client: ClientEntity -> dao.insertClient(client) },
        ),
        // Client details are mutable (staff assignment, default savings account, closure can
        // change the row) — expire the in-memory copy after a short window so a revisit
        // re-reads Room / refreshes network. Mirrors provideGroupStore.
        memoryPolicy = MemoryPolicy.builder<Int, ClientEntity>()
            .setExpireAfterWrite(5.minutes)
            .build(),
    )
}

// ---------------------------------------------------------------------------
// Inline mapping helpers — private to this file
// ---------------------------------------------------------------------------

/**
 * Back-fill `groupName`/`groupId` from the first associated group when the flat fields are
 * absent — verbatim port of the fix-up that lived in
 * `ClientDetailsRepositoryImp.getClient` before the Store5 cutover. Doing it in the fetcher
 * means the enriched entity is what lands in Room, so the group name is present offline.
 */
private fun ClientEntity.resolveGroup(): ClientEntity {
    if (groupName.isNullOrBlank() && !groups.isNullOrEmpty()) {
        groups?.firstOrNull()?.let { firstGroup ->
            return copy(
                groupName = firstGroup.name,
                groupId = firstGroup.id,
            )
        }
    }
    return this
}
