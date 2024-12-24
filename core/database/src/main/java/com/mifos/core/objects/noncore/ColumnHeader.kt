/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncore

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 16/06/14.
 */
@Parcelize
@Entity("ColumnHeader")
data class ColumnHeader(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    /**
     * columnCode will only be returned if columnDisplayType = "CODELOOKUP"
     * and null otherwise
     */
    var columnCode: String? = null,

    @ColumnInfo("columnDisplayType")
    var columnDisplayType: String? = null,

    @ColumnInfo("columnLabel")
    var columnLength: Int? = null,

    @ColumnInfo("dataTableColumnName")
    var dataTableColumnName: String? = null,

    @ColumnInfo("columnType")
    var columnType: String? = null,

    @ColumnInfo("columnNullable")
    var columnNullable: Boolean? = null,

    @ColumnInfo("columnPrimaryKey")
    var columnPrimaryKey: Boolean? = null,

    @ColumnInfo("registeredTableName")
    var registeredTableName: String? = null,

    /**
     * columnValues are actually Code Values that are either created by
     * system or defined manually by users
     */
    var columnValues: List<ColumnValue> = ArrayList(),
) : MifosBaseModel(), Parcelable
