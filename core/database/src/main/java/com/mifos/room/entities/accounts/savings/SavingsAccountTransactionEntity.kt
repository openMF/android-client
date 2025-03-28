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
import kotlinx.serialization.Serializable

@Entity(
    tableName = "TransactionTable",
    foreignKeys = [
        ForeignKey(
            entity = SavingsTransactionTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SavingsTransactionDateEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SavingAccountCurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
@Serializable
data class SavingsAccountTransactionEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: Int = 0,

    val savingsAccountId: Int? = null,

    val transactionType: SavingsTransactionTypeEntity? = null,

    val accountId: Int? = null,

    val accountNo: String? = null,

    val savingsTransactionDate: SavingsTransactionDateEntity? = null,

    val date: List<Int?> = emptyList(),

    val currency: SavingAccountCurrencyEntity? = null,

    val amount: Double? = null,

    val runningBalance: Double? = null,

    val reversed: Boolean? = null,
)
