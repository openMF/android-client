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
import kotlin.math.ceil

@Suppress("ReturnCount")
class MapDeserializer : JsonDeserializer<Map<String, Any>?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): Map<String, Any>? {
        return read(json) as Map<String, Any>?
    }

    fun read(element: JsonElement): Any? {
        if (element.isJsonArray) {
            val list: MutableList<Any?> = ArrayList()
            val arr = element.asJsonArray
            for (anArr in arr) {
                list.add(read(anArr))
            }
            return list
        } else if (element.isJsonObject) {
            val map: MutableMap<String, Any?> = HashMap()
            val obj = element.asJsonObject
            val entitySet = obj.entrySet()
            for ((key, value) in entitySet) {
                map[key] = read(value)
            }
            return map
        } else if (element.isJsonPrimitive) {
            val prim = element.asJsonPrimitive
            if (prim.isBoolean) {
                return prim.asBoolean
            } else if (prim.isString) {
                return prim.asString
            } else if (prim.isNumber) {
                val num = prim.asNumber
                // here you can handle double int/long values
                // and return any type you want
                // this solution will transform 3.0 float to long values
                return if (ceil(num.toDouble()) == num.toLong().toDouble()) {
                    num.toLong()
                } else {
                    num.toDouble()
                }
            }
        }
        return null
    }
}
