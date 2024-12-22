/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.templates.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 12/06/14.
 */
@Parcelize
@Entity("SavingsAccountTransactionTemplate")
class SavingsAccountTransactionTemplate(
    @PrimaryKey
    var accountId: Int? = null,

    @ColumnInfo("accountNo")
    var accountNo: String? = null,

    var date: List<Int> = ArrayList(),

    @ColumnInfo("reversed")
    var reversed: Boolean? = null,

    var paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption> = ArrayList(),

) : MifosBaseModel(), Parcelable {

    fun isReversed(): Boolean? {
        return reversed
    }
}
