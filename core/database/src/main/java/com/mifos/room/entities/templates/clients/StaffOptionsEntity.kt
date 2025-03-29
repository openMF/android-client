/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.clients

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "ClientTemplateStaffOptions")
data class StaffOptionsEntity(
    @PrimaryKey
    val id: Int = 0,

    val firstname: String = "",

    val lastname: String = "",

    val displayName: String = "",

    val officeId: Int = 0,

    val officeName: String = "",

    val isLoanOfficer: Boolean = false,

    val isActive: Boolean = false,
) : Parcelable
