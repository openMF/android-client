/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.basemodel.APIEndPoint

@Entity(tableName = "SavingAccountDepositType")
data class DepositType(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "code")
    var code: String? = null,

    @ColumnInfo(name = "value")
    var value: String? = null,
)

enum class ServerTypes(val id: Int, val code: String, val endpoint: String) {
    SAVINGS(100, "depositAccountType.savingsDeposit", APIEndPoint.SAVINGS_ACCOUNTS),
    FIXED(200, "depositAccountType.fixedDeposit", APIEndPoint.SAVINGS_ACCOUNTS),
    RECURRING(300, "depositAccountType.recurringDeposit", APIEndPoint.RECURRING_ACCOUNTS),
    ;

    companion object {
        fun fromId(id: Int): ServerTypes {
            for (type in entries) {
                if (type.id == id) {
                    return type
                }
            }
            return SAVINGS
        }
    }
}
