/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.di

import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.network.auth.api.AuthApi
import com.mifos.core.network.center.api.CenterApi
import com.mifos.core.network.charge.api.ChargeApi
import com.mifos.core.network.checkerinbox.api.CheckerInboxApi
import com.mifos.core.network.mifosclient.MifosApiClient
import com.mifos.core.network.client.api.ClientApi
import com.mifos.core.network.collectionsheet.api.CollectionSheetApi
import com.mifos.core.network.datatable.api.DataTableApi
import com.mifos.core.network.document.api.DocumentApi
import com.mifos.core.network.fixeddeposit.api.FixedDepositApi
import com.mifos.core.network.group.api.GroupApi
import com.mifos.core.network.loan.api.LoanApi
import com.mifos.core.network.note.api.NoteApi
import com.mifos.core.network.office.api.OfficeApi
import com.mifos.core.network.pathtracking.api.PathTrackingApi
import com.mifos.core.network.recurringdeposit.api.RecurringDepositApi
import com.mifos.core.network.report.api.ReportApi
import com.mifos.core.network.savings.api.SavingsApi
import com.mifos.core.network.search.api.SearchApi
import com.mifos.core.network.share.api.ShareApi
import com.mifos.core.network.staff.api.StaffApi
import com.mifos.core.network.survey.api.SurveyApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.koin.dsl.module
import template.core.base.network.httpClient
import template.core.base.network.setupDefaultHttpClient

val NetworkModule = module {

    // ─── HTTP client ──────────────────────────────────────────────────────
    //
    // Built on the template's `httpClient()` + `setupDefaultHttpClient(...)` helpers
    // (defined in `core-base/network`). Adds a Mifos-specific `defaultRequest` block
    // for the `Fineract-Platform-TenantId` header (read fresh per request from
    // [UserPreferencesRepository]) and the raw token-based Authorization header
    // (Fineract returns a non-bearer token; the template's bearer auth plugin
    // doesn't fit, so we set it as a raw header).
    single<HttpClient> {
        val prefManager = get<UserPreferencesRepository>()
        httpClient(
            config = {
                setupDefaultHttpClient(
                    baseUrl = prefManager.getServerConfig.value.getInstanceUrl(),
                    defaultHeaders = mapOf(
                        HttpHeaders.ContentType to "application/json",
                        HttpHeaders.Accept to "application/json",
                    ),
                ).invoke(this)

                defaultRequest {
                    header(
                        "Fineract-Platform-TenantId",
                        prefManager.getServerConfig.value.tenant,
                    )
                    prefManager.token?.takeIf { it.isNotBlank() }?.let { token ->
                        header(HttpHeaders.Authorization, token)
                    }
                }
            },
        )
    }

    // ─── Ktorfit instance ─────────────────────────────────────────────────
    single<Ktorfit> {
        Ktorfit.Builder()
            .baseUrl(get<UserPreferencesRepository>().getServerConfig.value.getInstanceUrl())
            .httpClient(get<HttpClient>())
            .build()
    }

    // ─── New per-domain Api wrapper + accessors ───────────────────────────
    single { MifosApiClient(ktorfit = get()) }
    single<AuthApi> { get<MifosApiClient>().authApi }
    single<CenterApi> { get<MifosApiClient>().centerApi }
    single<ChargeApi> { get<MifosApiClient>().chargeApi }
    single<CheckerInboxApi> { get<MifosApiClient>().checkerInboxApi }
    single<ClientApi> { get<MifosApiClient>().clientApi }
    single<CollectionSheetApi> { get<MifosApiClient>().collectionSheetApi }
    single<DataTableApi> { get<MifosApiClient>().dataTableApi }
    single<DocumentApi> { get<MifosApiClient>().documentApi }
    single<FixedDepositApi> { get<MifosApiClient>().fixedDepositApi }
    single<GroupApi> { get<MifosApiClient>().groupApi }
    single<LoanApi> { get<MifosApiClient>().loanApi }
    single<NoteApi> { get<MifosApiClient>().noteApi }
    single<OfficeApi> { get<MifosApiClient>().officeApi }
    single<PathTrackingApi> { get<MifosApiClient>().pathTrackingApi }
    single<RecurringDepositApi> { get<MifosApiClient>().recurringDepositApi }
    single<ReportApi> { get<MifosApiClient>().reportApi }
    single<SavingsApi> { get<MifosApiClient>().savingsApi }
    single<SearchApi> { get<MifosApiClient>().searchApi }
    single<ShareApi> { get<MifosApiClient>().shareApi }
    single<StaffApi> { get<MifosApiClient>().staffApi }
    single<SurveyApi> { get<MifosApiClient>().surveyApi }
}
