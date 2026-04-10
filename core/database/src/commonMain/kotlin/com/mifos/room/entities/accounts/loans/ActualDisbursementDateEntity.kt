/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Entity(
    tableName = "ActualDisbursementDateEntity",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
@Parcelize
data class ActualDisbursementDateEntity(
    @PrimaryKey(autoGenerate = true)
    val loanId: Int? = null,
    val year: Int? = null,
    val month: Int? = null,
    val date: Int? = null,
) : Parcelable
