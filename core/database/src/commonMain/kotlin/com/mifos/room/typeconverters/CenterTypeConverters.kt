/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.typeconverters

import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.group.CenterDateEntity
import com.mifos.room.entities.group.GroupDateEntity
import com.mifos.room.utils.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CenterTypeConverters {

    @TypeConverter
    fun fromCenterDate(centerDate: CenterDateEntity?): String? {
        return centerDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCenterDate(json: String?): CenterDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromDepositType(type: SavingAccountDepositTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toDepositType(json: String?): SavingAccountDepositTypeEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromGroupDate(groupDate: GroupDateEntity?): String? {
        return groupDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupDate(json: String?): GroupDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }
}
