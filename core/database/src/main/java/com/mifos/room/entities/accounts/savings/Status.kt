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
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "SavingsAccountStatus")
@Parcelize
data class Status(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo(name = "code")
    var code: String? = null,

    @ColumnInfo(name = "value")
    var value: String? = null,

    @ColumnInfo(name = "submittedAndPendingApproval")
    var submittedAndPendingApproval: Boolean? = null,

    @ColumnInfo(name = "approved")
    var approved: Boolean? = null,

    @ColumnInfo(name = "rejected")
    var rejected: Boolean? = null,

    @ColumnInfo(name = "withdrawnByApplicant")
    var withdrawnByApplicant: Boolean? = null,

    @ColumnInfo(name = "active")
    var active: Boolean? = null,

    @ColumnInfo(name = "closed")
    var closed: Boolean? = null,
) : Parcelable
