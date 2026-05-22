/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.di

import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.KtorHttpClient
import com.mifos.core.network.KtorfitClient
import com.mifos.core.network.MifosInterceptor
import com.mifos.core.network.auth.api.createAuthApi
import com.mifos.core.network.center.api.createCenterApi
import com.mifos.core.network.client.api.createClientApi
import com.mifos.core.network.document.api.createDocumentApi
import com.mifos.core.network.group.api.createGroupApi
import com.mifos.core.network.note.api.createNoteApi
import com.mifos.core.network.pathtracking.api.createPathTrackingApi
import com.mifos.core.network.search.api.createSearchApi
import com.mifos.core.network.utils.FlowConverterFactory
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import org.koin.dsl.module

val NetworkModule = module {
    single<HttpClient>(KtorClient) {
        val preferencesRepository = get<UserPreferencesRepository>()

        KtorHttpClient.config {
            install(Auth)
            install(MifosInterceptor) {
                repository = preferencesRepository
            }
        }
    }

    single<String>(BaseUrl) {
        val preferencesRepository = get<UserPreferencesRepository>()
        preferencesRepository.instanceUrl
    }

    single<KtorfitClient>(MifosClient) {
        KtorfitClient.builder()
            .httpClient(get(KtorClient))
            .baseURL(get<String>(BaseUrl))
            .build()
    }

    single { BaseApiManager.build(get()) }

    single { BaseApiManager(get(), get()) }

    single<Ktorfit> {
        Ktorfit.Builder()
            .baseUrl(get<String>(BaseUrl))
            .httpClient(get<HttpClient>(KtorClient))
            .converterFactories(FlowConverterFactory())
            .build()
    }

    // Per-resource Api factories (template's per-feature `core/network/<resource>/api/` pattern).
    // Each feature wave adds methods to the matching resource's interface; this DI list grows
    // as new resource APIs are introduced.
    single { get<Ktorfit>().createAuthApi() }
    single { get<Ktorfit>().createClientApi() }
    single { get<Ktorfit>().createCenterApi() }
    single { get<Ktorfit>().createDocumentApi() }
    single { get<Ktorfit>().createGroupApi() }
    single { get<Ktorfit>().createNoteApi() }
    single { get<Ktorfit>().createPathTrackingApi() }
    single { get<Ktorfit>().createSearchApi() }
}
