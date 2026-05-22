/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.staff.entity

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
    tableName = "ClientTemplateStaffOptions",
)
data class StaffOptionsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val firstname: String = "",

    val lastname: String = "",

    val displayName: String = "",

    val officeId: Int = 0,

    val officeName: String = "",

    val isLoanOfficer: Boolean = false,

    val isActive: Boolean = false,
)
