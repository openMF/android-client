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
import com.mifos.core.entity.accounts.savings.DepositType
import com.mifos.room.entities.Timeline
import com.mifos.room.entities.client.ChargeCalculationType
import com.mifos.room.entities.client.ChargeTimeType
import com.mifos.room.entities.client.ClientDate
import com.mifos.room.entities.client.Currency
import com.mifos.room.entities.client.Status
import com.mifos.room.entities.group.Group
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ClientTypeConverters {

    @TypeConverter
    fun fromChargeTimeType(chargeTimeType: ChargeTimeType?): String? {
        return chargeTimeType?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChargeTimeType(json: String?): ChargeTimeType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromClientDate(clientDate: ClientDate?): String? {
        return clientDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientDate(json: String?): ClientDate? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromChargeCalculationType(chargeCalculationType: ChargeCalculationType?): String? {
        return chargeCalculationType?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChargeCalculationType(json: String?): ChargeCalculationType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromCurrency(currency: Currency?): String? {
        return currency?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCurrency(json: String?): Currency? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromStatus(status: Status?): String? {
        return status?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toStatus(json: String?): Status? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListInt(date: List<Int?>?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListInt(json: String?): List<Int?>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromGroupActivationDateListInt(date: List<Int>?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupActivationDateListInt(json: String?): List<Int>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListGroup(date: List<Group?>?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListGroup(json: String?): List<Group?>? {
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

    @TypeConverter
    fun fromDepositType(depositType: DepositType?): String? {
        return depositType?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toDepositType(json: String?): DepositType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromMap(map: Map<String, Any>?): String? {
        return map?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, Any>? {
        return json?.let {
            Json.decodeFromString<JsonObject>(it)
                .mapValues { entry -> entry.value }
        }
    }
}
