/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

/**
 * A custom deserializer for converting JSON into a `Map<String, Any?>`.
 * This replaces the Gson-based deserializer with Kotlinx Serialization.
 */
object MapDeserializer {

    /**
     * Deserializes a JSON string into a `Map<String, Any?>`.
     *
     * @param jsonString The JSON string to deserialize.
     * @return A `Map<String, Any?>` representing the deserialized JSON.
     */
    fun deserialize(jsonString: String): Map<String, Any?> {
        val jsonElement = Json.parseToJsonElement(jsonString)
        return read(jsonElement) as? Map<String, Any?> ?: emptyMap()
    }

    /**
     * Recursively reads a [JsonElement] and converts it into a Kotlin object.
     *
     * @param jsonElement The JSON element to read.
     * @return A Kotlin object representing the JSON element.
     */
    private fun read(jsonElement: JsonElement): Any? {
        return when (jsonElement) {
            is JsonArray -> jsonElement.map { read(it) }
            is JsonObject -> jsonElement.entries.associate { (key, value) -> key to read(value) }
            is JsonPrimitive -> when {
                jsonElement.booleanOrNull != null -> jsonElement.boolean
                jsonElement.longOrNull != null -> jsonElement.long
                jsonElement.doubleOrNull != null -> jsonElement.double
                else -> jsonElement.content
            }
            else -> null
        }
    }
}
