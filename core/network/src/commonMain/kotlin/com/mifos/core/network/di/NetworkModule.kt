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

import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.KtorHttpClient
import com.mifos.core.network.KtorfitClient
import com.mifos.core.network.MifosInterceptor
import com.mifos.core.network.utils.FlowConverterFactory
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    single<KtorfitClient>(MifosClient) {
        val preferencesRepository = get<UserPreferencesRepository>()

        val serverConfigUrl = runBlocking {
            val serverConfig = preferencesRepository.getServerConfig.first()
            serverConfig.getInstanceUrl()
        }

        KtorfitClient.builder()
            .httpClient(get(KtorClient))
            .baseURL(serverConfigUrl)
            .build()
    }

    single { BaseApiManager.build(get()) }

    single { BaseApiManager(get(), get()) }

    single<Ktorfit> {
        val preferencesRepository = get<UserPreferencesRepository>()

        val serverConfigUrl = runBlocking {
            val serverConfig = preferencesRepository.getServerConfig.first()
            serverConfig.getInstanceUrl()
        }

        Ktorfit.Builder()
            .baseUrl(serverConfigUrl)
            .httpClient(get<HttpClient>(KtorClient))
            .converterFactories(FlowConverterFactory())
            .build()
    }

    single {
        val prefManager: UserPreferencesRepository = get()
        val baseManager = com.mifos.core.network.apimanager.BaseApiManager.getInstance()
        CoroutineScope(Dispatchers.Default).launch {
            val user = prefManager.userData.first()
            val serverConfig = prefManager.getServerConfig.first()
            Logger.e("serverConfigPro $serverConfig")
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
}