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

import com.mifos.core.model.objects.runreport.client.ClientReportTypeItem
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.room.dao.ReportCategoryDao
import com.mifos.room.entities.report.ReportCategoryEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration

/**
 * Composite Store key for the run-report category list — the request tuple Fineract scopes the
 * `reportCategoryList` call by. A `data class` so `equals`/`hashCode` are stable and structural,
 * which Store5 requires to key its in-memory cache and source-of-truth reader/writer correctly.
 */
data class ReportCategoryKey(
    val reportCategory: String,
    val genericResultSet: Boolean,
    val parameterType: Boolean,
)

/**
 * Build the read-only offline-first [Store] for the run-report category list, keyed by the
 * composite [ReportCategoryKey] `(reportCategory, genericResultSet, parameterType)` and streaming
 * the persisted [ReportCategoryEntity] rows for that scope.
 *
 * ### Full-list replace per key, not incremental append
 * Fineract's `reportCategoryList` endpoint returns the FULL category list for a given scope on every
 * call (no since-keyed delta). So the source-of-truth writer is a per-key wholesale
 * [ReportCategoryDao.replaceForKey] (delete-for-key + insert inside one `@Transaction`) rather than
 * the append-only path the loan ledger uses — this keeps the cache from retaining categories that
 * dropped off the server list for that scope. Mirrors `CheckerTaskStore`, but keyed by a composite
 * tuple (per-scope) rather than by [Unit] (single global list).
 *
 * ### Fetcher shape
 * [DataManagerRunReport.getReportCategories] returns a cold `Flow<List<ClientReportTypeItem>>`, so
 * [Fetcher.ofFlow] composes it straight through with a `map` that projects each domain
 * [ClientReportTypeItem] to a flat [ReportCategoryEntity] row stamped with the key's scoping tuple —
 * matching how `StoreFactory` constructs Flow-backed fetchers.
 *
 * NOTE: only the category LIST is cached here. The dynamic report OUTPUT (getRunReportWithQuery /
 * FullParameterListResponse) is deliberately EXCLUDED — it is per-query, non-listy, non-cacheable.
 *
 * @param api the network access seam ([DataManagerRunReport]) exposing the category-list call.
 * @param dao the Room DAO backing the local category-list source-of-truth.
 * @return a [Store] whose key is [ReportCategoryKey] and whose value is the cached category list.
 */
fun provideReportCategoryStore(
    api: DataManagerRunReport,
    dao: ReportCategoryDao,
): Store<ReportCategoryKey, List<ReportCategoryEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { key: ReportCategoryKey ->
            api.getReportCategories(
                key.reportCategory,
                key.genericResultSet,
                key.parameterType,
            ).map { list ->
                list.map { it.toEntity(key) }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: ReportCategoryKey ->
                dao.pageFlow(key.reportCategory, key.genericResultSet, key.parameterType)
            },
            writer = { key: ReportCategoryKey, rows: List<ReportCategoryEntity> ->
                dao.replaceForKey(key.reportCategory, key.genericResultSet, key.parameterType, rows)
            },
            delete = { key: ReportCategoryKey ->
                dao.deleteForKey(key.reportCategory, key.genericResultSet, key.parameterType)
            },
            deleteAll = { dao.deleteAll() },
        ),
        // Category list per scope — immutable rows once written; never expire in-memory.
        memoryPolicy = MemoryPolicy.builder<ReportCategoryKey, List<ReportCategoryEntity>>()
            .setExpireAfterWrite(Duration.INFINITE)
            .build(),
    )
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (fetcher side)
// ---------------------------------------------------------------------------

private fun ClientReportTypeItem.toEntity(key: ReportCategoryKey): ReportCategoryEntity =
    ReportCategoryEntity(
        // rowId autogenerates
        reportCategory = key.reportCategory,
        genericResultSet = key.genericResultSet,
        parameterType = key.parameterType,
        parameterId = parameterId,
        parameterName = parameterName,
        itemReportCategory = reportCategory,
        reportId = reportId,
        reportName = reportName,
        reportParameterName = reportParameterName,
        reportSubtype = reportSubtype,
        reportType = reportType,
    )
