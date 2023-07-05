/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.utils

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Rajan Maurya on 28,March,2016.
 */
class JsonDateSerializer : JsonSerializer<Date?> {
    override fun serialize(
        src: Date?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formattedTime = output.format(src)
        return JsonPrimitive(formattedTime)
    }
}