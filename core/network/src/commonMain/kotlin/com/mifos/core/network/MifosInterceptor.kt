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

import com.mifos.core.datastore.UserPreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey

class MifosInterceptor(
    private val repository: UserPreferencesRepository,
) {
    companion object Plugin : HttpClientPlugin<ConfigMifos, MifosInterceptor> {

        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        private const val CONTENT_TYPE = "Content-Type"

        override val key: AttributeKey<MifosInterceptor> = AttributeKey("MifosInterceptor")

        override fun install(plugin: MifosInterceptor, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                val tenant = plugin.repository.getServerConfig.value.tenant
                context.header(CONTENT_TYPE, "application/json")
                context.header("Accept", "application/json")
                context.header(HEADER_TENANT, tenant)

                plugin.repository.token?.let { token ->
                    if (token.isNotEmpty()) {
                        context.headers[HEADER_AUTH] = token
                    }
                }
            }
        }

        override fun prepare(block: ConfigMifos.() -> Unit): MifosInterceptor {
            val config = ConfigMifos().apply(block)
            return MifosInterceptor(config.repository)
        }
    }
}

class ConfigMifos {
    lateinit var repository: UserPreferencesRepository
}
