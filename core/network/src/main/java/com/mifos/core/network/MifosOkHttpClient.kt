/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mifos.core.datastore.PrefManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by Rajan Maurya on 16/06/16.
 */
class MifosOkHttpClient(private val prefManager: PrefManager) {
    val okHttpClient: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            // Enable Full Body Logging
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            // Setting Timeout 30 Seconds
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.readTimeout(60, TimeUnit.SECONDS)

            // Interceptor :> Full Body Logger and ApiRequest Header
            builder.addInterceptor(logger)
            builder.addInterceptor(MifosInterceptor(prefManager))
            builder.addNetworkInterceptor(StethoInterceptor())
            return builder.build()
        }
}
