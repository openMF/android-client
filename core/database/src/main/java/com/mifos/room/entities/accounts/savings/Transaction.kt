/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Transaction",
    foreignKeys = [
        ForeignKey(
            entity = TransactionType::class,
            parentColumns = ["id"],
            childColumns = ["transactionType"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SavingsTransactionDate::class,
            parentColumns = ["transactionId"],
            childColumns = ["savingsTransactionDate"],
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
// @TypeConverters(NullableIntegerListConverter::class, CurrencyTypeConverter::class)
data class Transaction(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "savingsAccountId")
    var savingsAccountId: Int? = null,

    @ColumnInfo(name = "transactionType", index = true)
    var transactionType: TransactionType? = null,

    @ColumnInfo(name = "accountId")
    var accountId: Int? = null,

    @ColumnInfo(name = "accountNo")
    var accountNo: String? = null,

    @ColumnInfo(name = "savingsTransactionDate")
    @Transient
    var savingsTransactionDate: SavingsTransactionDate? = null,

    @ColumnInfo(name = "date")
    var date: List<Int?> = ArrayList(),

    @ColumnInfo(name = "currency", index = true)
    var currency: Currency? = null,

    @ColumnInfo(name = "amount")
    var amount: Double? = null,

    @ColumnInfo(name = "runningBalance")
    var runningBalance: Double? = null,

    @ColumnInfo(name = "reversed")
    var reversed: Boolean? = null,
)
