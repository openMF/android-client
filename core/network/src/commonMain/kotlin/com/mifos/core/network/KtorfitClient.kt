package com.mifos.core.network

import com.mifos.core.network.utils.FlowConverterFactory
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

class KtorfitClient(
    ktorfit: Ktorfit,
) {
    class Builder internal constructor() {
        private lateinit var baseURL: String
        private lateinit var httpClient: HttpClient

        fun baseURL(baseURL: String): Builder {
            this.baseURL = baseURL
            return this
        }

        fun httpClient(ktorHttpClient: HttpClient): Builder {
            this.httpClient = ktorHttpClient
            return this
        }

        fun build(): KtorfitClient {
            val ktorfitBuilder = Ktorfit.Builder()
                .httpClient(httpClient)
                .baseUrl(baseURL)
                .converterFactories(FlowConverterFactory())
                .build()

            return KtorfitClient(ktorfitBuilder)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}