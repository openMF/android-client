/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.objects.Changes
import kotlinx.parcelize.Parcelize

@Entity(tableName = "LoanRepaymentResponse")
@Parcelize
data class LoanRepaymentResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val officeId: Int? = null,
    val clientId: Int? = null,
    val loanId: Int? = null,
    val resourceId: Int? = null,
    val changes: Changes? = null,
) : Parcelable
