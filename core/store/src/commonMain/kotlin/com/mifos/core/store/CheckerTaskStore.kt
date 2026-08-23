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

import com.mifos.core.model.objects.checkerinboxtask.CheckerTask
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.room.dao.CheckerTaskDao
import com.mifos.room.entities.checkerinbox.CheckerTaskEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration

/**
 * Build the read-only offline-first [Store] for the checker-inbox task list.
 *
 * ### Key = [Unit] — a single global list
 * The checker inbox is one global collection of pending maker-checker tasks; no server-side
 * scoping params (`actionName` / `entityName` / `resourceId`) are used today — filtering is
 * done client-side in the screen. So the Store is keyed by [Unit] (a single logical page),
 * mirroring how the reference `LoanTransactionStore` keys by a scalar.
 *
 * ### Full-list replace, not incremental append
 * Fineract's checker endpoint returns the FULL current task list on every call (no
 * since-keyed delta), and tasks disappear once approved/rejected. So the source-of-truth
 * writer is a wholesale [CheckerTaskDao.replaceAll] (delete-all + insert inside one
 * `@Transaction`) rather than the append-only path the loan ledger uses — this keeps the
 * cache from retaining tasks that have dropped off the server list.
 *
 * ### Fetcher shape
 * [DataManagerCheckerInbox.getCheckerTaskList] returns a cold `Flow<List<CheckerTask>>`, so
 * [Fetcher.ofFlow] composes it straight through with a `map` that projects each domain
 * [CheckerTask] to a flat [CheckerTaskEntity] row — matching how `StoreFactory` constructs
 * Flow-backed fetchers.
 *
 * @param api the network access seam ([DataManagerCheckerInbox]) exposing the list call.
 * @param dao the Room DAO backing the local checker-task source-of-truth.
 * @return a [Store] whose key is [Unit] and whose value is the cached task list.
 */
fun provideCheckerTaskStore(
    api: DataManagerCheckerInbox,
    dao: CheckerTaskDao,
): Store<Unit, List<CheckerTaskEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { _: Unit ->
            api.getCheckerTaskList().map { list ->
                list.map { it.toEntity() }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { _: Unit -> dao.pageFlow() },
            writer = { _: Unit, rows: List<CheckerTaskEntity> -> dao.replaceAll(rows) },
            delete = { _: Unit -> dao.deleteAll() },
            deleteAll = { dao.deleteAll() },
        ),
        // Global task list — immutable rows once written; never expire in-memory.
        memoryPolicy = MemoryPolicy.builder<Unit, List<CheckerTaskEntity>>()
            .setExpireAfterWrite(Duration.INFINITE)
            .build(),
    )
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (fetcher side)
// ---------------------------------------------------------------------------

private fun CheckerTask.toEntity(): CheckerTaskEntity = CheckerTaskEntity(
    id = id,
    madeOnDate = madeOnDate,
    processingResult = processingResult,
    maker = maker,
    actionName = actionName,
    entityName = entityName,
    resourceId = resourceId,
)
