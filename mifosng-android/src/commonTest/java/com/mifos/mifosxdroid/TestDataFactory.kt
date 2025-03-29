/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

/**
 * Converted by using Kotlinx Serialization
 * Replaces Gson with kotlinx.serialization.json.Json
 */
class TestDataFactory {

    /**
     * Generic Method to Deserialize JSON Object into a POJO.
     * @param jsonName Name of the JSON file in test/resources.
     * @param <T> Return type.
     * @return Returns the object of type T by deserializing the JSON from resources.
     */
    inline fun <reified T> getObjectTypePojo(jsonName: String?): T {
        val value = javaClass.classLoader.getResourceAsStream(jsonName)
            ?: throw IllegalArgumentException("Resource not found: $jsonName")
        val reader = InputStreamReader(value)
        val jsonString = reader.readText()
        reader.close()
        return Json.decodeFromString(jsonString)
    }

    /**
     * Generic Method to Deserialize JSON into a List.
     * @param jsonName Name of the JSON file in test/resources.
     * @param <T> Return type.
     * @return Returns the list of type T by deserializing the JSON from resources.
     */
    inline fun <reified T> getListTypePojo(jsonName: String?): List<T> {
        val value = javaClass.classLoader.getResourceAsStream(jsonName)
            ?: throw IllegalArgumentException("Resource not found: $jsonName")
        val reader = InputStreamReader(value)
        val jsonString = reader.readText()
        reader.close()
        return Json.decodeFromString(jsonString)
    }
}
