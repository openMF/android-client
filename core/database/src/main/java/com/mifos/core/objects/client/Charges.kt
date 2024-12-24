/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 2/15/2016.
 */
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@Parcelize
@Entity("Charges")
data class Charges(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("clientId")
    var clientId: Int? = null,

    @ColumnInfo("loanId")
    var loanId: Int? = null,

    @ColumnInfo("chargeId")
    var chargeId: Int? = null,

    @ColumnInfo("name")
    var name: String? = null,

    @ColumnInfo("chargeTypeId")
    @Embedded
    var chargeTimeType: ChargeTimeType? = null,

    @ColumnInfo("chargeDueDate")
    @Embedded
    var chargeDueDate: ClientDate? = null,

    var dueDate: List<Int> = ArrayList(),

    @ColumnInfo("chargeCalculationType")
    @Embedded
    var chargeCalculationType: ChargeCalculationType? = null,

    @ColumnInfo("currency")
    @Embedded
    var currency: Currency? = null,

    @ColumnInfo("amount")
    var amount: Double? = null,

    @ColumnInfo("amountPaid")
    var amountPaid: Double? = null,

    @ColumnInfo("amountWaived")
    var amountWaived: Double? = null,

    @ColumnInfo("amountWrittenOff")
    var amountWrittenOff: Double? = null,

    @ColumnInfo("amountOutstanding")
    var amountOutstanding: Double? = null,

    @ColumnInfo("penalty")
    var penalty: Boolean? = null,

    @ColumnInfo("active")
    var active: Boolean? = null,

    @ColumnInfo("paid")
    var paid: Boolean? = null,

    @ColumnInfo("waived")
    var waived: Boolean? = null,
) : MifosBaseModel(), Parcelable {

    val formattedDueDate: String
        get() {
            val pattern = "%s-%s-%s"
            if (dueDate.size > 2) {
                return String.format(
                    pattern,
                    dueDate[0],
                    dueDate[1],
                    dueDate[2],
                )
            }
            return "No Due Date"
        }
}
