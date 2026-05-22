/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.charge.entity

import com.mifos.room.client.entity.ClientDateEntity
import kotlinx.serialization.Serializable
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Created by nellyk on 2/15/2016.
 */
@Serializable
@Entity(
    tableName = "Charges",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [],
)
data class ChargesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val clientId: Int? = null,

    val loanId: Int? = null,

    val chargeId: Int? = null,

    val name: String? = null,

    val chargeTimeType: ChargeTimeTypeEntity? = null,

    val chargeDueDate: ClientDateEntity? = null,

    val dueDate: List<Int>? = null,

    val chargeCalculationType: ChargeCalculationTypeEntity? = null,

    val currency: ClientChargeCurrencyEntity? = null,

    val amount: Double? = null,

    val amountPaid: Double? = null,

    val amountWaived: Double? = null,

    val amountWrittenOff: Double? = null,

    val amountOutstanding: Double? = null,

    val penalty: Boolean? = null,

    val active: Boolean? = null,

    val paid: Boolean? = null,

    val waived: Boolean? = null,
) {

    val formattedDueDate: String
        get() = if (dueDate?.size == 3) {
            "${dueDate[0]}-${dueDate[1]}-${dueDate[2]}"
        } else {
            "No Due Date"
        }
}
