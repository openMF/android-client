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

import com.mifos.room.savings.entity.PaymentTypeOptionEntity
import kotlinx.serialization.Serializable
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "SavingsAccountTransactionTemplate",
)
data class SavingsAccountTransactionTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int? = null,

    val accountNo: String? = null,

    val date: List<Int> = emptyList(),

    val reversed: Boolean? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity> = emptyList(),
) {

    fun isReversed(): Boolean? {
        return reversed
    }
}
