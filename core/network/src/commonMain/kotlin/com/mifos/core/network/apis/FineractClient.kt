/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.apis

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FineractClient private constructor(
    private val httpClient: HttpClient,
    private val ktorfit: Ktorfit,
) {

    val clientIdentifiers: ClientIdentifierApi = ktorfit.createClientIdentifierApi()
    val centers: CentersApi = ktorfit.createCentersApi()
    val clients: ClientApi = ktorfit.createClientApi()
    val dataTables: DataTablesApi = ktorfit.createDataTablesApi()
    val groups: GroupsApi = ktorfit.createGroupsApi()
    val offices: OfficesApi = ktorfit.createOfficesApi()
    val staff: StaffApi = ktorfit.createStaffApi()

    class Builder internal constructor() {
        /**
         * Obtain the internal OkHttp Builder. This method is typically not required to be invoked for simple API
         * usages, but can be a handy back door for non-trivial advanced customizations of the API client.
         *
         * @return the [FineractClient] which [.build] will use.
         */

        /**
         * Obtain the internal Retrofit Builder. This method is typically not required to be invoked for simple API
         * usages, but can be a handy back door for non-trivial advanced customizations of the API client.
         *
         * @return the [FineractClient] which [.build] will use.
         */

        private lateinit var baseURL: String
        private var tenant: String? = null
        private var loginUsername: String? = null
        private var loginPassword: String? = null
        private var insecure: Boolean = false

        fun baseURL(baseURL: String): Builder {
            this.baseURL = baseURL
            return this
        }

        fun tenant(tenant: String?): Builder {
            this.tenant = tenant
            return this
        }

        fun basicAuth(username: String?, password: String?): Builder {
            this.loginUsername = username
            this.loginPassword = password
            return this
        }

        fun inSecure(insecure: Boolean): Builder {
            this.insecure = insecure
            return this
        }

        fun build(): FineractClient {
            val ktorClient = HttpClient {
                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        },
                    )
                }

                install(Auth) {
                    basic {
                        credentials {
                            BasicAuthCredentials(
                                username = loginUsername.toString(),
                                password = loginPassword.toString(),
                            )
                        }
                    }
                }

                defaultRequest {
                    contentType(ContentType.Application.Json)
                    headers {
                        append("Accept", "application/json")
                        tenant?.let {
                            append("fineract-platform-tenantid", it)
                        }
                    }
                }
            }

            val ktorfitBuilder = Ktorfit.Builder()
                .httpClient(ktorClient)
                .baseUrl(baseURL)
                .build()

            return FineractClient(ktorClient, ktorfitBuilder)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
