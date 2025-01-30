/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.utils.typeconverters

import androidx.room.TypeConverter
import com.mifos.room.entities.group.Center
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ListTypeConverters {

    @TypeConverter
    fun fromIntList(value: String): ArrayList<Int?> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toIntList(list: ArrayList<Int?>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun fromCenterList(centers: List<Center?>): String {
        return centers.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCenterList(json: String): List<Center?> {
        return json.let { Json.decodeFromString(it) }
    }
}
