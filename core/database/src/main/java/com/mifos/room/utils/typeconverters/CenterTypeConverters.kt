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
import com.mifos.room.entities.accounts.savings.DepositType
import com.mifos.room.entities.group.CenterDate
import com.mifos.room.entities.group.GroupDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CenterTypeConverters {

    @TypeConverter
    fun fromCenterDate(centerDate: CenterDate?): String? {
        return centerDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCenterDate(json: String?): CenterDate? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromDepositType(type: DepositType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toDepositType(json: String?): DepositType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromGroupDate(groupDate: GroupDate?): String? {
        return groupDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupDate(json: String?): GroupDate? {
        return json?.let { Json.decodeFromString(it) }
    }
}
