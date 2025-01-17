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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DueDateConverter {
    @TypeConverter
    fun fromListToString(dueDate: List<Int>): String {
        return Json.encodeToString(dueDate)
    }

    @TypeConverter
    fun fromStringToList(dueDateString: String): List<Int> {
        return Json.decodeFromString(dueDateString)
    }
}
