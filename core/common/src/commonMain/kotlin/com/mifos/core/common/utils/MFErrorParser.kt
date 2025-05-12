/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import com.mifos.core.model.objects.error.MifosError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

object MFErrorParser {
    const val LOG_TAG: String = "MFErrorParser"

    private val json: Json = Json { ignoreUnknownKeys = true }

    private fun parseError(serverResponse: String?): MifosError {
        return json.decodeFromString(serverResponse ?: "{}")
    }

    suspend fun errorMessage(throwableError: Throwable): String {
        return try {
            when (throwableError) {
                is ClientRequestException, is ServerResponseException -> {
                    val response = (throwableError as? ClientRequestException)?.response
                        ?: (throwableError as? ServerResponseException)?.response
                    val errorBody = response?.bodyAsText() ?: ""
                    parseError(errorBody).errors.firstOrNull()?.defaultUserMessage
                        ?: "Something went wrong!"
                }

                else -> throwableError.message ?: "Unknown error"
            }
        } catch (exception: Exception) {
            "Error processing response"
        }
    }
}
