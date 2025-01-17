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
import androidx.room.PrimaryKey

@Entity(tableName = "TransactionType")
data class TransactionType(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "code")
    var code: String? = null,

    @ColumnInfo(name = "value")
    var value: String? = null,

    @ColumnInfo(name = "deposit")
    var deposit: Boolean? = null,

    @ColumnInfo(name = "withdrawal")
    var withdrawal: Boolean? = null,

    @ColumnInfo(name = "interestPosting")
    var interestPosting: Boolean? = null,

    @ColumnInfo(name = "feeDeduction")
    var feeDeduction: Boolean? = null,

    @ColumnInfo(name = "initiateTransfer")
    var initiateTransfer: Boolean? = null,

    @ColumnInfo(name = "approveTransfer")
    var approveTransfer: Boolean? = null,

    @ColumnInfo(name = "withdrawTransfer")
    var withdrawTransfer: Boolean? = null,

    @ColumnInfo(name = "rejectTransfer")
    var rejectTransfer: Boolean? = null,

    @ColumnInfo(name = "overdraftInterest")
    var overdraftInterest: Boolean? = null,

    @ColumnInfo(name = "writtenoff")
    var writtenoff: Boolean? = null,

    @ColumnInfo(name = "overdraftFee")
    var overdraftFee: Boolean? = null,
)
