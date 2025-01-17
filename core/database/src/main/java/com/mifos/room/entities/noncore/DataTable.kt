/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.noncore

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.entity.noncore.ColumnHeader
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("DataTable")
data class DataTable(
    @ColumnInfo("applicationTableName")
    var applicationTableName: String? = null,

    @ColumnInfo("columnHeaderData")
    var columnHeaderData: List<ColumnHeader> = ArrayList(),

    @PrimaryKey
    @ColumnInfo("registeredTableName")
    var registeredTableName: String? = null,
) : Parcelable
