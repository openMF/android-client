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

import com.mifos.room.center.entity.CenterEntity
import com.mifos.room.charge.entity.SavingsCharge
import com.mifos.room.savings.entity.SavingsAccountTransactionEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.room3.TypeConverter

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
    fun fromCenterList(centers: List<CenterEntity?>): String {
        return centers.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCenterList(json: String): List<CenterEntity?> {
        return json.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun toListOfInts(json: String?): List<Int?>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListOfTransactions(list: List<SavingsAccountTransactionEntity>?): String? {
        return list?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListOfTransactions(json: String?): List<SavingsAccountTransactionEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListOfCharges(list: List<SavingsCharge?>?): String? {
        return list?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListOfCharges(json: String?): List<SavingsCharge?>? {
        return json?.let { Json.decodeFromString(it) }
    }
}
