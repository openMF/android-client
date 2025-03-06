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
import com.mifos.core.model.objects.account.saving.InterestCalculationDaysInYearType
import com.mifos.core.model.objects.account.saving.InterestCalculationType
import com.mifos.core.model.objects.account.saving.InterestCompoundingPeriodType
import com.mifos.core.model.objects.account.saving.InterestPostingPeriodType
import com.mifos.core.model.objects.account.saving.LockinPeriodFrequencyType
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.savings.SavingsTransactionDate
import com.mifos.room.entities.accounts.savings.TransactionType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// todo add missing converters
class SavingsTypeConverters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toTransactionType(json: String?): TransactionType? {
        return json?.let { Json.decodeFromString<TransactionType>(it) }
    }

    @TypeConverter
    fun fromSavingsTransactionDate(date: SavingsTransactionDate?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSavingsTransactionDate(json: String?): SavingsTransactionDate? {
        return json?.let { Json.decodeFromString<SavingsTransactionDate>(it) }
    }

    @TypeConverter
    fun fromInterestCalculationDaysInYearType(
        type: InterestCalculationDaysInYearType?,
    ): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCalculationDaysInYearType(json: String?): InterestCalculationDaysInYearType? {
        return json?.let { Json.decodeFromString<InterestCalculationDaysInYearType>(it) }
    }

    @TypeConverter
    fun fromInterestCalculationType(type: InterestCalculationType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCalculationType(json: String?): InterestCalculationType? {
        return json?.let { Json.decodeFromString<InterestCalculationType>(it) }
    }

    @TypeConverter
    fun fromInterestCompoundingPeriodType(
        type: InterestCompoundingPeriodType?,
    ): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCompoundingPeriodType(
        json: String?,
    ): InterestCompoundingPeriodType? {
        return json?.let { Json.decodeFromString<InterestCompoundingPeriodType>(it) }
    }

    @TypeConverter
    fun fromInterestPostingPeriodType(
        type: InterestPostingPeriodType?,
    ): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestPostingPeriodType(
        json: String?,
    ): InterestPostingPeriodType? {
        return json?.let { Json.decodeFromString<InterestPostingPeriodType>(it) }
    }

    @TypeConverter
    fun fromLockinPeriodFrequencyType(type: LockinPeriodFrequencyType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLockinPeriodFrequencyType(json: String?): LockinPeriodFrequencyType? {
        return json?.let { Json.decodeFromString<LockinPeriodFrequencyType>(it) }
    }

    @TypeConverter
    fun fromPaymentTypeOption(paymentTypeOption: PaymentTypeOption?): String? {
        return paymentTypeOption?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toPaymentTypeOption(json: String?): PaymentTypeOption? {
        return json?.let { Json.decodeFromString<PaymentTypeOption>(it) }
    }
}
