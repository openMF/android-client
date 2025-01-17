/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.organisation

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Staff")
data class Staff(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "firstname")
    var firstname: String? = null,

    @ColumnInfo(name = "lastname")
    var lastname: String? = null,

    @ColumnInfo(name = "mobileNo")
    var mobileNo: String? = null,

    @ColumnInfo(name = "displayName")
    var displayName: String? = null,

    @ColumnInfo(name = "officeId")
    var officeId: Int? = null,

    @ColumnInfo(name = "officeName")
    var officeName: String? = null,

    @ColumnInfo(name = "isLoanOfficer")
    var isLoanOfficer: Boolean? = null,

    @ColumnInfo(name = "isActive")
    var isActive: Boolean? = null,
) : Parcelable
