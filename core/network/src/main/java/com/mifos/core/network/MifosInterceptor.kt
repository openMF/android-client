/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

import com.mifos.core.datastore.PrefManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * @author fomenkoo
 */
class MifosInterceptor(private val prefManager: PrefManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chianrequest = chain.request()
        val builder = chianrequest.newBuilder()
            .header(HEADER_TENANT, prefManager.getTenant())
        if (prefManager.isAuthenticated()) builder.header(HEADER_AUTH, prefManager.getToken())
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
    }
}