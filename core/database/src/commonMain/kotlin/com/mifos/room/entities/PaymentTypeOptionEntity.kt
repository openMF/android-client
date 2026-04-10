/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Entity(
    tableName = "PaymentTypeOption",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
@Serializable
@Parcelize
data class PaymentTypeOptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val description: String? = null,
    val isCashPayment: Boolean? = null,
    val position: Int? = null,
) : Parcelable
//    : Comparable<PaymentTypeOptionEntity> {
//
//    override fun compareTo(other: PaymentTypeOptionEntity): Int {
//        return position?.compareTo(other.position ?: 0) ?: 0
//    }
//
//    override fun toString(): String {
//        return "PaymentTypeOption{" +
//                "id=$id, " +
//                "name='$name', " +
//                "description='$description', " +
//                "isCashPayment=$isCashPayment, " +
//                "position=$position" +
//                '}'
//    }
// }
