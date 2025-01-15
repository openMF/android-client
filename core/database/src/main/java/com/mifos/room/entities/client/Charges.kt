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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.json.Json

/**
 * Created by nellyk on 2/15/2016.
 */
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@Parcelize
@Entity(
    tableName = "Charges",
    foreignKeys = [
        ForeignKey(
            entity = ChargeTimeType::class,
            parentColumns = ["id"],
            childColumns = ["chargeTimeType"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClientDate::class,
            parentColumns = ["clientId"],
            childColumns = ["chargeDueDate"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ChargeCalculationType::class,
            parentColumns = ["id"],
            childColumns = ["chargeCalculationType"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["code"],
            childColumns = ["currency"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Charges(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "clientId")
    var clientId: Int? = null,

    @ColumnInfo(name = "loanId")
    var loanId: Int? = null,

    @ColumnInfo(name = "chargeId")
    var chargeId: Int? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "chargeTimeType")
    var chargeTimeType: ChargeTimeType? = null,

    @ColumnInfo(name = "chargeDueDateId")
    var chargeDueDate: ClientDate? = null,

    @ColumnInfo(name = "dueDate")
    var dueDate: String? = null,

    @ColumnInfo(name = "chargeCalculationType")
    var chargeCalculationType: ChargeCalculationType? = null,

    @ColumnInfo(name = "currency")
    var currency: Currency? = null,

    @ColumnInfo(name = "amount")
    var amount: Double? = null,

    @ColumnInfo(name = "amountPaid")
    var amountPaid: Double? = null,

    @ColumnInfo(name = "amountWaived")
    var amountWaived: Double? = null,

    @ColumnInfo(name = "amountWrittenOff")
    var amountWrittenOff: Double? = null,

    @ColumnInfo(name = "amountOutstanding")
    var amountOutstanding: Double? = null,

    @ColumnInfo(name = "penalty")
    var penalty: Boolean? = null,

    @ColumnInfo(name = "active")
    var active: Boolean? = null,

    @ColumnInfo(name = "paid")
    var paid: Boolean? = null,

    @ColumnInfo(name = "waived")
    var waived: Boolean? = null,
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
                    return String.format(pattern, dueDateList[0], dueDateList[1], dueDateList[2])
                }
            }
            return "No Due Date"
        }
}
