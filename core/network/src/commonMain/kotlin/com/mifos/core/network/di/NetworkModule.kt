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

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.UserPreferencesRepositoryImpl
import com.mifos.core.network.BaseUrl
import com.mifos.core.network.KtorfitClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.dsl.module
//import org.mifos.core.apimanager.BaseApiManager

val NetworkModule = module {

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            get(),
            get(
                named(
                    MifosDispatchers.IO.name,
                ),
            ),
            get(named(MifosDispatchers.Unconfined)),
        )
    }

    single<KtorfitClient>(MifosClient) {
        KtorfitClient.builder()
            .httpClient(get(KtorBaseClient))
            .baseURL(BaseUrl().url)
            .build()
    }

//    single { com.mifos.core.network.BaseApiManager(get()) }
//
//    single { BaseApiManager }

//    single {
//        val prefManager: UserPreferencesRepository = get()
//        val baseManager = BaseApiManager.getInstance()
//        CoroutineScope(Dispatchers.IO).launch {
//            val user = prefManager.userData.first()
//            val serverConfig = prefManager.getServerConfig.first()
//            baseManager.createService(
//                user.username ?: "",
//                user.password ?: "",
//                serverConfig.getInstanceUrl().dropLast(3),
//                serverConfig.tenant,
//                false,
//            )
//        }
//        baseManager
//    }
//

//    single {
//        val okHttpCallFactory by lazy { get<Call.Factory>() }
//        ImageLoader.Builder(androidContext())
//            .callFactory { okHttpCallFactory }
//            .apply {
//                logger(DebugLogger())
//            }.build()
//    }
}
