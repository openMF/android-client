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

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.utils.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ClientTypeConverters {
    @TypeConverter
    fun fromCurrency(type: Currency?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCurrency(json: String?): Currency? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromLoanAccountEntity(type: LoanAccountEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLoanAccountEntity(json: String?): LoanAccountEntity? {
        return json?.let { Json.decodeFromString(it) }
    }
}
