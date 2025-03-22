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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.entities.PaymentTypeOptionEntity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "SavingsAccountTransactionTemplate")
data class SavingsAccountTransactionTemplateEntity(
    @PrimaryKey
    val accountId: Int? = null,

    val accountNo: String? = null,

    val date: List<Int> = emptyList(),

    val reversed: Boolean? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity> = emptyList(),
) : Parcelable {

    fun isReversed(): Boolean? {
        return reversed
    }
}
