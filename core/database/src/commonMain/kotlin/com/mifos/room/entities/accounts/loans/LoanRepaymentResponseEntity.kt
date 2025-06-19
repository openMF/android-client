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

import com.mifos.core.model.objects.Changes
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "LoanRepaymentResponse",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
@Parcelize
data class LoanRepaymentResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val officeId: Int? = null,
    val clientId: Int? = null,
    val loanId: Int? = null,
    val resourceId: Int? = null,
    @IgnoredOnParcel
    val changes: Changes? = null,
) : Parcelable
