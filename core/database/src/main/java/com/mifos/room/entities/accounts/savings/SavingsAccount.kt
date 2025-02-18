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
    var id: Int? = null,

    @ColumnInfo(name = "clientId")
    @Transient
    var clientId: Long = 0,

    @ColumnInfo(name = "groupId")
    @Transient
    var groupId: Long = 0,

    @ColumnInfo(name = "centerId")
    var centerId: Long = 0,

    @ColumnInfo(name = "accountNo")
    var accountNo: String? = null,

    @ColumnInfo(name = "productId")
    var productId: Int? = null,

    @ColumnInfo(name = "productName")
    var productName: String? = null,

    @ColumnInfo(name = "status", index = true)
    var status: Status? = null,

    @ColumnInfo(name = "currency", index = true)
    var currency: Currency? = null,

    @ColumnInfo(name = "accountBalance")
    var accountBalance: Double? = null,

    @ColumnInfo(name = "depositType", index = true)
    var depositType: DepositType? = null,
) : Parcelable
