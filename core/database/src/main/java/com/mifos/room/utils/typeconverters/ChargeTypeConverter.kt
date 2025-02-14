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
import com.mifos.room.entities.client.ChargeCalculationType
import com.mifos.room.entities.client.ChargeTimeType
import com.mifos.room.entities.client.ClientDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Pronay Sarker on 14/02/2025 (9:18 PM)
 */

class ChargeTypeConverter {

    @TypeConverter
    fun fromChargeTimeType(type: ChargeTimeType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChargeTimeType(json: String?): ChargeTimeType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromClientDate(date: ClientDate?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientDate(json: String?): ClientDate? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromChargeCalculationType(type: ChargeCalculationType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChargeCalculationType(json: String?): ChargeCalculationType? {
        return json?.let { Json.decodeFromString(it) }
    }
}
