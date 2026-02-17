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

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
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
    tableName = "SavingsAccount",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = SavingsAccountStatusEntity::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = SavingAccountCurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["currency"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
        ForeignKey(
            entity = SavingAccountDepositTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["depositType"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
    ],
)
@Parcelize
@Serializable
data class SavingsAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val clientId: Long = 0,

    val groupId: Long = 0,

    val centerId: Long = 0,

    val accountNo: String? = null,

    val productId: Int? = null,

    val shortProductName: String? = null,

    val productName: String? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val status: SavingsAccountStatusEntity? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val currency: SavingAccountCurrencyEntity? = null,

    val accountBalance: Double? = null,

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val depositType: SavingAccountDepositTypeEntity? = null,

    val lastActiveTransactionDate: List<Int>? = null,
) : Parcelable
