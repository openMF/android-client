/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.savings.entity

import com.mifos.core.network.APIEndPoint
import kotlinx.serialization.Serializable
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "SavingAccountDepositType",
)
@Serializable
data class SavingAccountDepositTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val code: String? = null,

    val value: String? = null,
) {

    val isRecurring: Boolean
        get() = ServerTypes.RECURRING.id == id
    val endpoint: String
        get() = ServerTypes.fromId(id).endpoint
    val serverType: ServerTypes
        get() = ServerTypes.fromId(id)

    enum class ServerTypes(val id: Int, val code: String, val endpoint: String) {
        SAVINGS(100, "depositAccountType.savingsDeposit", APIEndPoint.SAVINGS_ACCOUNTS),
        FIXED(200, "depositAccountType.fixedDeposit", APIEndPoint.FIXED_DEPOSIT),
        RECURRING(300, "depositAccountType.recurringDeposit", APIEndPoint.RECURRING_ACCOUNTS),
        ;

        companion object {
            fun fromId(id: Int?): ServerTypes {
                for (type in entries) {
                    if (type.id == id) {
                        return type
                    }
                }
                return SAVINGS
            }
        }
    }
}
