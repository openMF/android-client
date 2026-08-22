/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.organisation

import kotlinx.serialization.Serializable
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "Staff",
)
data class StaffEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val firstname: String? = null,

    val lastname: String? = null,

    val mobileNo: String? = null,

    val displayName: String? = null,

    val officeId: Int? = null,

    val officeName: String? = null,

    val isLoanOfficer: Boolean? = null,

    val isActive: Boolean? = null,
)
