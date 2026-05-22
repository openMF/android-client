/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.loan.entity

import com.mifos.core.model.objects.Changes
import com.mifos.core.model.utils.IgnoredOnParcel
import kotlinx.serialization.Serializable
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Serializable
@Entity(
    tableName = "LoanRepaymentResponse",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
data class LoanRepaymentResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val officeId: Int? = null,
    val clientId: Int? = null,
    val loanId: Int? = null,
    val resourceId: Int? = null,
    @IgnoredOnParcel
    val changes: Changes? = null,
)
