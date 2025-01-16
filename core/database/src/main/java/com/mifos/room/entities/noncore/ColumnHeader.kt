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
import com.mifos.core.entity.noncore.ColumnValue
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ColumnHeader")
data class ColumnHeader(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "columnCode")
    var columnCode: String? = null,

    @ColumnInfo(name = "columnDisplayType")
    var columnDisplayType: String? = null,

    @ColumnInfo(name = "columnLength")
    var columnLength: Int? = null,

    @ColumnInfo(name = "dataTableColumnName")
    var dataTableColumnName: String? = null,

    @ColumnInfo(name = "columnType")
    var columnType: String? = null,

    @ColumnInfo(name = "columnNullable")
    var columnNullable: Boolean? = null,

    @ColumnInfo(name = "columnPrimaryKey")
    var columnPrimaryKey: Boolean? = null,

    @ColumnInfo(name = "registeredTableName")
    var registeredTableName: String? = null,

    @ColumnInfo(name = "columnValues")
    var columnValues: List<ColumnValue> = ArrayList(),
) : Parcelable
