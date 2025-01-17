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
import androidx.room.PrimaryKey

@Entity(tableName = "LoanStatus")
data class Status(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "code")
    var code: String? = null,

    @ColumnInfo(name = "value")
    var value: String? = null,

    @ColumnInfo(name = "pendingApproval")
    var pendingApproval: Boolean? = null,

    @ColumnInfo(name = "waitingForDisbursal")
    var waitingForDisbursal: Boolean? = null,

    @ColumnInfo(name = "active")
    var active: Boolean? = null,

    @ColumnInfo(name = "closedObligationsMet")
    var closedObligationsMet: Boolean? = null,

    @ColumnInfo(name = "closedWrittenOff")
    var closedWrittenOff: Boolean? = null,

    @ColumnInfo(name = "closedRescheduled")
    var closedRescheduled: Boolean? = null,

    @ColumnInfo(name = "closed")
    var closed: Boolean? = null,

    @ColumnInfo(name = "overpaid")
    var overpaid: Boolean? = null,
)
