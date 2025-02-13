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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OfficeTypeConverters {
    @TypeConverter
    fun fromOpeningDateList(list: List<Int?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toOpeningDateList(json: String): List<Int?> {
        return Gson().fromJson(json, object : TypeToken<List<Int?>>() {}.type)
    }
}
