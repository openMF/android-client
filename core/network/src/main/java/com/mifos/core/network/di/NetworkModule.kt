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

import coil.ImageLoader
import coil.util.DebugLogger
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.UserPreferencesRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.mifos.core.apimanager.BaseApiManager

val NetworkModule = module {

    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get(), Dispatchers.IO, Dispatchers.Unconfined) }

    single { com.mifos.core.network.BaseApiManager(get()) }

    single { BaseApiManager }

    single {
        val prefManager: UserPreferencesRepository = get()

        val user = runBlocking { prefManager.userData.first() }
        val serverConfig = runBlocking { prefManager.getServerConfig.first() }

        val baseManager = BaseApiManager.getInstance()
        if (serverConfig != null) {
            baseManager.createService(
                user.username ?: "",
                user.password ?: "",
                serverConfig.getInstanceUrl().dropLast(3) ?: "",
                serverConfig.tenant ?: "",
                false,
            )
        }
        baseManager
    }

    single<Call.Factory> { OkHttpClient.Builder().build() }

    single {
        val okHttpCallFactory by lazy { get<Call.Factory>() }
        ImageLoader.Builder(androidContext())
            .callFactory { okHttpCallFactory }
            .apply {
                logger(DebugLogger())
            }.build()
    }
}
