/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.model.objects.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.model.objects.checkerinboxtask.CheckerTask
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.room.entities.checkerinbox.CheckerTaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Offline-first read path for the checker inbox (Store5 pilot, mirrors
 * [LoanTransactionsRepositoryImp][com.mifos.core.data.repositoryImp.loan.LoanTransactionsRepositoryImp]).
 * The task list is served through the Store5 checker-task store (qualifier
 * [AppStoreRegistry.CheckerTasks][kpt.core.store.AppStoreRegistry.CheckerTasks]) instead of
 * a raw `DataManager.getCheckerTaskList()` network call. `StoreReadRequest.cached(refresh =
 * true)` emits the Room-persisted rows immediately (so the inbox renders offline from cache)
 * AND triggers a background network refresh when connectivity is available (SWR). The
 * fetcher's network error is intentionally swallowed — Store5's source-of-truth reader still
 * emits the cached (possibly empty) list, so the screen shows cached rows offline rather
 * than a network error.
 *
 * The write methods (approve / reject / delete / search template) stay on the raw
 * [DataManagerCheckerInbox] path — the store is a read-only cache. See `residual_risks`: an
 * approve/reject mutates server state, so callers still re-invoke [loadCheckerTasks] to force
 * a `refresh = true` re-fetch (the ViewModel already does this after each write).
 */
class CheckerInboxRepositoryImp(
    private val checkerTaskStore: Store<Unit, List<CheckerTaskEntity>>,
    private val dataManagerCheckerInbox: DataManagerCheckerInbox,
) : CheckerInboxRepository {

    override fun loadCheckerTasks(
        actionName: String?,
        entityName: String?,
        resourceId: Int?,
    ): Flow<List<CheckerTask>> {
        // Params are ignored today (as before this migration) — the checker inbox is a single
        // global list keyed by Unit; filtering is done client-side in the screen.
        return checkerTaskStore
            .stream(StoreReadRequest.cached(key = Unit, refresh = true))
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.map { it.toDomain() }
                    // Offline-first: fetch error is non-fatal — the SoT reader emits the cached
                    // (possibly empty) list as a separate Data response.
                    else -> null
                }
            }
    }

    override suspend fun approveCheckerEntry(auditId: Int): GenericResponse {
        return dataManagerCheckerInbox.approveCheckerEntry(auditId)
    }

    override suspend fun rejectCheckerEntry(auditId: Int): GenericResponse {
        return dataManagerCheckerInbox.rejectCheckerEntry(auditId)
    }

    override suspend fun deleteCheckerEntry(auditId: Int): GenericResponse {
        return dataManagerCheckerInbox.deleteCheckerEntry(auditId)
    }

    override suspend fun loadSearchTemplate(): CheckerInboxSearchTemplate {
        TODO("Not yet implemented")
    }
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (repo side)
// ---------------------------------------------------------------------------

private fun CheckerTaskEntity.toDomain(): CheckerTask = CheckerTask(
    id = id,
    madeOnDate = madeOnDate,
    processingResult = processingResult,
    maker = maker,
    actionName = actionName,
    entityName = entityName,
    resourceId = resourceId,
)
