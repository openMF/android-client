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

import com.mifos.core.datastore.PrefManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author fomenkoo
 */
class MifosInterceptor(private val prefManager: PrefManager) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chianrequest = chain.request()
        val builder = chianrequest.newBuilder()
            .header(HEADER_TENANT, prefManager.serverConfig.tenant)
        if (prefManager.isAuthenticated) builder.header(HEADER_AUTH, prefManager.token)
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
    }
}
