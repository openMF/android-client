/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.group.converter

import com.mifos.core.model.objects.timeline.Timeline
import com.mifos.room.group.entity.GroupDateEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.room3.TypeConverter

/**
 * Created by Pronay Sarker on 17/02/2025 (7:45 AM)
 */
class GroupTypeConverters {

    @TypeConverter
    fun fromGroupDate(date: GroupDateEntity?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupDate(json: String?): GroupDateEntity? {
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
