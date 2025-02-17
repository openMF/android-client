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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class LoanAccount(
    @PrimaryKey
    val id: Int? = null,

    val clientId: Long = 0,

    val groupId: Long = 0,

    val centerId: Long = 0,

    val accountNo: String? = null,

    val externalId: String? = null,

    val productId: Int? = null,

    val productName: String? = null,

    @ColumnInfo(index = true)
    val status: Status? = null,

    @ColumnInfo(index = true)
    val loanType: LoanType? = null,

    val loanCycle: Int? = null,

    val inArrears: Boolean? = null,
) : Parcelable
