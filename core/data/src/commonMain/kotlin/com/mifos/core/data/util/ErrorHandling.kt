/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.util

import com.mifos.core.model.objects.error.MifosError
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

/**
 * Generic function to extract error messages from API responses
 */
suspend fun extractErrorMessage(response: HttpResponse): String {
    val responseText = response.bodyAsText()
    return try {
        val json = Json { ignoreUnknownKeys = true }
        val errorResponse = json.decodeFromString<MifosError>(responseText)
        errorResponse.errors.firstOrNull()?.defaultUserMessage
            ?: errorResponse.defaultUserMessage
            ?: Error.MSG_NOT_FOUND
    } catch (e: Exception) {
        Error.FAILED_TO_PARSE_ERROR_RESPONSE
    }
}

data object Error {
    const val MSG_NOT_FOUND = "Message Not Found"
    const val FAILED_TO_PARSE_ERROR_RESPONSE = "Failed to parse error response"
}
