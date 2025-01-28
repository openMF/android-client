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
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "LoanStatus")
@Serializable
@Parcelize
data class Status(
    @PrimaryKey
    val id: Int? = null,

    @ColumnInfo(name = "code")
    val code: String? = null,

    @ColumnInfo(name = "value")
    val value: String? = null,

    @ColumnInfo(name = "pendingApproval")
    val pendingApproval: Boolean? = null,

    @ColumnInfo(name = "waitingForDisbursal")
    val waitingForDisbursal: Boolean? = null,

    @ColumnInfo(name = "active")
    val active: Boolean? = null,

    @ColumnInfo(name = "closedObligationsMet")
    val closedObligationsMet: Boolean? = null,

    @ColumnInfo(name = "closedWrittenOff")
    val closedWrittenOff: Boolean? = null,

    @ColumnInfo(name = "closedRescheduled")
    val closedRescheduled: Boolean? = null,

    @ColumnInfo(name = "closed")
    val closed: Boolean? = null,

    @ColumnInfo(name = "overpaid")
    val overpaid: Boolean? = null,
) : Parcelable
