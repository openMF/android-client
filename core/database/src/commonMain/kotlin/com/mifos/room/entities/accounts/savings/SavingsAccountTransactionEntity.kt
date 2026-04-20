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
import template.core.base.database.CollationSequence.UNSPECIFIED
import template.core.base.database.ColumnInfo
import template.core.base.database.ColumnInfoTypeAffinity.INHERIT_FIELD_NAME
import template.core.base.database.ColumnInfoTypeAffinity.UNDEFINED
import template.core.base.database.ColumnInfoTypeAffinity.VALUE_UNSPECIFIED
import template.core.base.database.Entity
import template.core.base.database.ForeignKey
import template.core.base.database.ForeignKeyAction
import template.core.base.database.PrimaryKey

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
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = SavingsTransactionDateEntity::class,
            parentColumns = ["transactionId"],
            childColumns = ["id"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = SavingAccountCurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
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
