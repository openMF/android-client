/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.ColumnInfo
import com.mifos.room.utils.Entity
import com.mifos.room.utils.ForeignKey
import com.mifos.room.utils.ForeignKeyAction
import com.mifos.room.utils.INHERIT_FIELD_NAME
import com.mifos.room.utils.PrimaryKey
import com.mifos.room.utils.UNDEFINED
import com.mifos.room.utils.UNSPECIFIED
import com.mifos.room.utils.VALUE_UNSPECIFIED
import kotlinx.serialization.json.Json

/**
 * Created by nellyk on 2/15/2016.
 */
@Parcelize
@Entity(
    tableName = "Charges",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = ChargeTimeTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["chargeTimeType"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = ClientDateEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["chargeDueDate"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = ChargeCalculationTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = ClientChargeCurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
    ],
)
data class ChargesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val id: Int = 0,

    val clientId: Int? = null,

    val loanId: Int? = null,

    val chargeId: Int? = null,

    val name: String? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val chargeTimeType: ChargeTimeTypeEntity? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val chargeDueDate: ClientDateEntity? = null,

    val dueDate: String? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val chargeCalculationType: ChargeCalculationTypeEntity? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
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
) : Parcelable {

    val formattedDueDate: String
        get() {
            val pattern = "%s-%s-%s"

            val dueDateList = try {
                dueDate?.let { Json.decodeFromString<List<Int>>(it) }
            } catch (e: kotlinx.serialization.SerializationException) {
                emptyList()
            }

            if (dueDateList != null) {
                if (dueDateList.size > 2) {
                    return "${dueDateList[0]}-${dueDateList[1]}-${dueDateList[2]}"
                }
            }
            return "No Due Date"
        }
}
