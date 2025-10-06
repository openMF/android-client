/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

fun Map<String, JsonElement>.extractErrorMessage(key: String): String? {
    // Check in 'errors' array
    val errors = this["errors"]
    if (errors is JsonArray) {
        val firstError = errors.firstOrNull()?.jsonObject
        firstError?.get(key)?.let { msgElem ->
            if (msgElem is JsonPrimitive && msgElem.isString) return msgElem.content
        }
    }
    return null
}
