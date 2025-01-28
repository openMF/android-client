/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Status::class,
            parentColumns = ["id"],
            childColumns = ["status"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = LoanType::class,
            parentColumns = ["id"],
            childColumns = ["loanType"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class LoanAccount(
    @PrimaryKey
    val id: Int? = null,

    @ColumnInfo(name = "clientId")
    val clientId: Long = 0,

    @ColumnInfo(name = "groupId")
    val groupId: Long = 0,

    @ColumnInfo(name = "centerId")
    val centerId: Long = 0,

    @ColumnInfo(name = "accountNo")
    val accountNo: String? = null,

    @ColumnInfo(name = "externalId")
    val externalId: String? = null,

    @ColumnInfo(name = "productId")
    val productId: Int? = null,

    @ColumnInfo(name = "productName")
    val productName: String? = null,

    @ColumnInfo(name = "status", index = true)
    val status: Status? = null,

    @ColumnInfo(name = "loanType", index = true)
    val loanType: LoanType? = null,

    @ColumnInfo(name = "loanCycle")
    val loanCycle: Int? = null,

    @ColumnInfo(name = "inArrears")
    val inArrears: Boolean? = null,
)
