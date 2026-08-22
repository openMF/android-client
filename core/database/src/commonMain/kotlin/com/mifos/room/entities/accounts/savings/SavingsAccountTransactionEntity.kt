/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import kotlinx.serialization.Serializable
import androidx.room3.ColumnInfo.Companion.UNSPECIFIED
import androidx.room3.ColumnInfo
import androidx.room3.ColumnInfo.Companion.INHERIT_FIELD_NAME
import androidx.room3.ColumnInfo.Companion.UNDEFINED
import androidx.room3.ColumnInfo.Companion.VALUE_UNSPECIFIED
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "TransactionTable",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = SavingsTransactionTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = SavingsTransactionDateEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = SavingAccountCurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false,
        ),
    ],
)
@Serializable
data class SavingsAccountTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
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
