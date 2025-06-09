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

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "SavingAccountDepositType",
)
@Parcelize
@Serializable
data class SavingAccountDepositTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val code: String? = null,

    val value: String? = null,
) : Parcelable {

    val isRecurring: Boolean
        get() = ServerTypes.RECURRING.id == id
    val endpoint: String
        get() = ServerTypes.fromId(id).endpoint
    val serverType: ServerTypes
        get() = ServerTypes.fromId(id)

    enum class ServerTypes(val id: Int, val code: String, val endpoint: String) {
        SAVINGS(100, "depositAccountType.savingsDeposit", APIEndPoint.SAVINGS_ACCOUNTS),
        FIXED(200, "depositAccountType.fixedDeposit", APIEndPoint.SAVINGS_ACCOUNTS),
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
