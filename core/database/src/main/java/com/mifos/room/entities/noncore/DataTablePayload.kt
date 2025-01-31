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
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "DataTablePayload")
data class DataTablePayload(

    @PrimaryKey(autoGenerate = true)
    @Transient
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "clientCreationTime")
    @Transient
    var clientCreationTime: Long? = null,

    @ColumnInfo(name = "dataTableString")
    @Transient
    var dataTableString: String? = null,

    @ColumnInfo(name = "registeredTableName")
    var registeredTableName: String? = null,

    @ColumnInfo(name = "data")
    var data: HashMap<String, @RawValue Any>? = null,
) : Parcelable
