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
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 12/06/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
class SavingsAccountTransactionTemplate(
    @PrimaryKey
    var accountId: Int? = null,

    @Column
    var accountNo: String? = null,

    var date: List<Int> = ArrayList(),

    @Column
    var reversed: Boolean? = null,

    var paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption> = ArrayList(),

) : MifosBaseModel(), Parcelable {

    fun isReversed(): Boolean? {
        return reversed
    }
}
