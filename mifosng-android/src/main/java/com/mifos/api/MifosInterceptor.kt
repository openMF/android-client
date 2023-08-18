/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api

import com.mifos.utils.PrefManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author fomenkoo
 */
class MifosInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chianrequest = chain.request()
        val builder = chianrequest.newBuilder()
            .header(HEADER_TENANT, PrefManager.getTenant())
        if (PrefManager.isAuthenticated()) builder.header(HEADER_AUTH, PrefManager.getToken())
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
    }
}