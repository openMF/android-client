/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("Transaction")
data class Transaction(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("savingsAccountId")
    @Transient
    var savingsAccountId: Int? = null,

    @ColumnInfo("transactionType")
    @Embedded
    var transactionType: TransactionType? = null,

    var accountId: Int? = null,

    var accountNo: String? = null,

    @ColumnInfo("savingsTransactionDate")
    @Embedded
    @Transient
    var savingsTransactionDate: SavingsTransactionDate? = null,

    var date: List<Int?> = ArrayList(),

    @ColumnInfo("currency")
    @Embedded
    var currency: Currency? = null,

    @ColumnInfo("amount")
    var amount: Double? = null,

    @ColumnInfo("runningBalance")
    var runningBalance: Double? = null,

    var reversed: Boolean? = null,
) : MifosBaseModel(), Parcelable
