/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.di

import coil3.ImageLoader
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.BaseUrl
import com.mifos.core.network.KtorHttpClient
import com.mifos.core.network.KtorfitClient
import com.mifos.core.network.MifosInterceptor
import com.mifos.core.network.utils.ImageLoaderUtils
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.dsl.module

val NetworkModule = module {

    single<HttpClient>(MifosClient) {
        val preferencesRepository = get<UserPreferencesRepository>()

        KtorHttpClient.config {
            install(Auth)
            install(MifosInterceptor) {
                repository = preferencesRepository
            }
        }
    }

    single<KtorfitClient>(MifosClient) {
        KtorfitClient.builder()
            .httpClient(get(KtorBaseClient))
            .baseURL(BaseUrl().url)
            .build()
    }

    single { BaseApiManager.build(get()) }

    single { BaseApiManager(get(), get()) }

    single<Ktorfit> {
        Ktorfit.Builder()
            .baseUrl(BaseUrl().url)
            .httpClient(get<HttpClient>(MifosClient))
            .build()
    }

    single {
        val prefManager: UserPreferencesRepository = get()
        val baseManager = com.mifos.core.network.apimanager.BaseApiManager.getInstance()
        CoroutineScope(Dispatchers.Default).launch {
            val user = prefManager.userData.first()
            val serverConfig = prefManager.getServerConfig.first()
            baseManager.createService(
                user.username ?: "",
                user.password ?: "",
                serverConfig.getInstanceUrl().dropLast(3),
                serverConfig.tenant,
                false,
            )
        }
        baseManager
    }

    single { (context: Any) ->
        ImageLoaderUtils(
            prefManager = get<UserPreferencesRepository>(),
            imageLoader = get<ImageLoader>(),
            context = context,
        )
    }
}
