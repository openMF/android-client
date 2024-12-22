/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("SavingsAccountStatus")
data class Status(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("code")
    var code: String? = null,

    @ColumnInfo("value")
    var value: String? = null,

    @ColumnInfo("submittedAndPendingApproval")
    var submittedAndPendingApproval: Boolean? = null,

    @ColumnInfo("approved")
    var approved: Boolean? = null,

    @ColumnInfo("rejected")
    var rejected: Boolean? = null,

    @ColumnInfo("withdrawnByApplicant")
    var withdrawnByApplicant: Boolean? = null,

    @ColumnInfo("active")
    var active: Boolean? = null,

    @ColumnInfo("closed")
    var closed: Boolean? = null,
) : MifosBaseModel(), Parcelable
