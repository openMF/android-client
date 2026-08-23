/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.typeconverters

import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.group.CenterDateEntity
import com.mifos.room.entities.group.GroupDateEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.room3.ColumnTypeConverter

class CenterTypeConverters {

    @ColumnTypeConverter
    fun fromCenterDate(centerDate: CenterDateEntity?): String? {
        return centerDate?.let { Json.encodeToString(it) }
    }

    @ColumnTypeConverter
    fun toCenterDate(json: String?): CenterDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @ColumnTypeConverter
    fun fromDepositType(type: SavingAccountDepositTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @ColumnTypeConverter
    fun toDepositType(json: String?): SavingAccountDepositTypeEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @ColumnTypeConverter
    fun fromGroupDate(groupDate: GroupDateEntity?): String? {
        return groupDate?.let { Json.encodeToString(it) }
    }

    @ColumnTypeConverter
    fun toGroupDate(json: String?): GroupDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }
}
