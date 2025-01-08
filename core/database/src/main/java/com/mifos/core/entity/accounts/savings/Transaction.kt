/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.accounts.savings

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class Transaction(
    @PrimaryKey
    var id: Int? = null,

    @Column
    @Transient
    var savingsAccountId: Int? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var transactionType: TransactionType? = null,

    var accountId: Int? = null,

    var accountNo: String? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @Transient
    var savingsTransactionDate: SavingsTransactionDate? = null,

    var date: List<Int?> = ArrayList(),

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var currency: Currency? = null,

    @Column
    var amount: Double? = null,

    @Column
    var runningBalance: Double? = null,

    var reversed: Boolean? = null,
) : MifosBaseModel(), Parcelable
