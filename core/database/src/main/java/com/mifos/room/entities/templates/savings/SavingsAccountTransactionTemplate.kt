/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.entities.PaymentTypeOption
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "SavingsAccountTransactionTemplate")
data class SavingsAccountTransactionTemplate(
    @PrimaryKey
    @ColumnInfo(name = "accountId")
    var accountId: Int? = null,

    @ColumnInfo(name = "accountNo")
    var accountNo: String? = null,

    @ColumnInfo(name = "date")
    var date: List<Int> = ArrayList(),

    @ColumnInfo(name = "reversed")
    var reversed: Boolean? = null,

    @ColumnInfo(name = "paymentTypeOptions")
    var paymentTypeOptions: List<PaymentTypeOption> = ArrayList(),
) : Parcelable {

    fun isReversed(): Boolean? {
        return reversed
    }
}
