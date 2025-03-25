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

import android.content.Context
import androidx.core.os.trace
import coil.ImageLoader
import coil.util.DebugLogger
import com.mifos.core.common.utils.getInstanceUrl
import okhttp3.Call
import okhttp3.OkHttpClient
import org.koin.dsl.module
import org.mifos.core.apimanager.BaseApiManager


val networkModule = module {

    single { com.mifos.core.datastore.PrefManager() }

    single {
        com.mifos.core.network.BaseApiManager(get())
    }

    single {
        val prefManager: com.mifos.core.datastore.PrefManager = get()
        val usernamePassword: Pair<String, String> = prefManager.usernamePassword
        val baseManager = BaseApiManager.getInstance()
        baseManager.createService(
            usernamePassword.first,
            usernamePassword.second,
            prefManager.getServerConfig.getInstanceUrl().dropLast(3),
            prefManager.getServerConfig.tenant,
            false,
        )
        baseManager
    }

    single<Call.Factory> {
        trace("MifosHttpClient") { OkHttpClient.Builder().build() }
    }

    single { (context: Context) ->
        ImageLoader.Builder(context)
            .callFactory { get<Call.Factory>() }
            .apply {
                logger(DebugLogger())
            }
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModusle {

    @Provides
    @Singleton
    fun provideBaseApiManager(prefManager: com.mifos.core.datastore.PrefManager): com.mifos.core.network.BaseApiManager {
        return com.mifos.core.network.BaseApiManager(prefManager)
    }

    @Provides
    @Singleton
    fun provideSdkBaseApiManager(prefManager: com.mifos.core.datastore.PrefManager): BaseApiManager {
        val usernamePassword: Pair<String, String> = prefManager.usernamePassword
        val baseManager = BaseApiManager.getInstance()
        baseManager.createService(
            usernamePassword.first,
            usernamePassword.second,
            prefManager.getServerConfig.getInstanceUrl().dropLast(3),
            prefManager.getServerConfig.tenant,
            false,
        )
        return baseManager
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = trace("MifosHttpClient") {
        OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext context: Context,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .callFactory { okHttpCallFactory.get() }
            .apply {
                logger(DebugLogger())
            }.build()
    }
}
