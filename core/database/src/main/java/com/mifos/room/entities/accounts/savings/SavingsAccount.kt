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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "SavingsAccount",
    foreignKeys = [
        ForeignKey(
            entity = Status::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["code"],
            childColumns = ["currency"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DepositType::class,
            parentColumns = ["id"],
            childColumns = ["depositType"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
@Parcelize
data class SavingsAccount(
    @PrimaryKey
    val id: Int? = null,

    @Transient
    val clientId: Long = 0,

    @Transient
    val groupId: Long = 0,

    val centerId: Long = 0,

    val accountNo: String? = null,

    val productId: Int? = null,

    val productName: String? = null,

    @ColumnInfo(index = true)
    val status: Status? = null,

    @ColumnInfo(index = true)
    val currency: Currency? = null,

    val accountBalance: Double? = null,

    @ColumnInfo(index = true)
    val depositType: DepositType? = null,
) : Parcelable
