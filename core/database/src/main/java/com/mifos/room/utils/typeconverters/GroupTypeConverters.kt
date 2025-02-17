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
import com.mifos.room.entities.Timeline
import com.mifos.room.entities.group.GroupDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Pronay Sarker on 17/02/2025 (7:45 AM)
 */
class GroupTypeConverters {

    @TypeConverter
    fun fromGroupDate(date: GroupDate?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupDate(json: String?): GroupDate? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromTimeline(timeline: Timeline?): String? {
        return timeline?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toTimeline(json: String?): Timeline? {
        return json?.let { Json.decodeFromString(it) }
    }
}
