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
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientTemplateStaffOptions")
data class StaffOptions(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "firstname")
    val firstname: String = "",

    @ColumnInfo(name = "lastname")
    val lastname: String = "",

    @ColumnInfo(name = "displayName")
    val displayName: String = "",

    @ColumnInfo(name = "officeId")
    val officeId: Int = 0,

    @ColumnInfo(name = "officeName")
    val officeName: String = "",

    @SerializedName("isLoanOfficer")
    @ColumnInfo(name = "isLoanOfficer")
    var isLoanOfficer: Boolean = false,

    @SerializedName("isActive")
    @ColumnInfo(name = "isActive")
    var isActive: Boolean = false,
) : Parcelable
