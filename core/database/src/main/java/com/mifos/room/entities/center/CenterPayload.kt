/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.center

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "CenterPayload")
data class CenterPayload(
    @PrimaryKey(autoGenerate = true)
    @Transient
    var id: Int = 0,

    @ColumnInfo(name = "errorMessage")
    @Transient
    var errorMessage: String? = null,

    @ColumnInfo(name = "dateFormat")
    var dateFormat: String? = null,

    @ColumnInfo(name = "locale")
    var locale: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "officeId")
    var officeId: Int? = null,

    @ColumnInfo(name = "active")
    var active: Boolean = false,

    @ColumnInfo(name = "activationDate")
    var activationDate: String? = null,
) : Parcelable
