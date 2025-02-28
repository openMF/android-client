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
    tableName = "TransactionTable",
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
data class Transaction(
    @PrimaryKey
    val id: Int? = null,

    val savingsAccountId: Int? = null,

    @ColumnInfo(index = true)
    val transactionType: TransactionType? = null,

    val accountId: Int? = null,

    val accountNo: String? = null,

    @Transient
    val savingsTransactionDate: SavingsTransactionDate? = null,

    val date: List<Int?> = emptyList(),

    @ColumnInfo(index = true)
    val currency: Currency? = null,

    val amount: Double? = null,

    val runningBalance: Double? = null,

    val reversed: Boolean? = null,
)
