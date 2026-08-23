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

import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.model.objects.runreport.client.ClientReportTypeItem
import com.mifos.core.store.ReportCategoryKey
import com.mifos.room.entities.report.ReportCategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Offline-first read path for the run-report category list (Store5 adoption, mirrors
 * [CheckerInboxRepositoryImp] / [LoanTransactionsRepositoryImp][com.mifos.core.data.repositoryImp.loan.LoanTransactionsRepositoryImp]).
 * The category list is served through the Store5 report-category store (qualifier
 * [AppStoreRegistry.ReportCategories][kpt.core.store.AppStoreRegistry.ReportCategories]) instead of a
 * raw `DataManagerRunReport.getReportCategories()` network call. `StoreReadRequest.cached(refresh =
 * true)` emits the Room-persisted rows immediately (so the report list renders offline from cache)
 * AND triggers a background network refresh when connectivity is available (SWR). The fetcher's
 * network error is intentionally swallowed — Store5's source-of-truth reader still emits the cached
 * (possibly empty) list, so the screen shows cached rows offline rather than a network error.
 *
 * Only the category LIST is cached. The dynamic report OUTPUT (getRunReportWithQuery /
 * FullParameterListResponse) is a per-query result and is served by [ReportDetailRepository], NOT
 * through this store.
 */
class ReportCategoryRepositoryImp(
    private val reportCategoryStore: Store<ReportCategoryKey, List<ReportCategoryEntity>>,
) : ReportCategoryRepository {

    override fun getReportCategories(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean,
    ): Flow<List<ClientReportTypeItem>> {
        return reportCategoryStore
            .stream(
                StoreReadRequest.cached(
                    key = ReportCategoryKey(reportCategory, genericResultSet, parameterType),
                    refresh = true,
                ),
            )
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.map { it.toDomain() }
                    // Offline-first: fetch error is non-fatal — the SoT reader emits the cached
                    // (possibly empty) list as a separate Data response.
                    else -> null
                }
            }
    }
}

// ---------------------------------------------------------------------------
// Inline mapping helper — private to this file (repo side)
// ---------------------------------------------------------------------------

private fun ReportCategoryEntity.toDomain(): ClientReportTypeItem = ClientReportTypeItem(
    parameterId = parameterId,
    parameterName = parameterName,
    reportCategory = itemReportCategory,
    reportId = reportId,
    reportName = reportName,
    reportParameterName = reportParameterName,
    reportSubtype = reportSubtype,
    reportType = reportType,
)
