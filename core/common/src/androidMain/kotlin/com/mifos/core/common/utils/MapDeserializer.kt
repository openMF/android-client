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

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class MapDeserializer : JsonDeserializer<Map<String, Any>?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Map<String, Any>? {
        return read(json) as Map<String, Any>?
    }

    fun read(jsonElement: JsonElement): Any? {
        return when {
            jsonElement.isJsonArray -> {
                val list = jsonElement.asJsonArray.map { read(it) }
                list
            }
            jsonElement.isJsonObject -> {
                jsonElement.asJsonObject.entrySet().associate { (key, value) -> key to read(value) }
            }
            jsonElement.isJsonPrimitive -> {
                val prim = jsonElement.asJsonPrimitive
                when {
                    prim.isBoolean -> prim.asBoolean
                    prim.isString -> prim.asString
                    prim.isNumber -> {
                        val num = prim.asNumber
                        if (Math.ceil(num.toDouble()) == num.toLong().toDouble()) num.toLong() else num.toDouble()
                    }
                    else -> null
                }
            }
            else -> null
        }
    }
}
