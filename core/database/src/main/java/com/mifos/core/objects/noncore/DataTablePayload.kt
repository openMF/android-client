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
import kotlinx.parcelize.RawValue

/**
 * Created by Tarun on 1/28/2017.
 */
@Parcelize
@Entity("DataTablePayload")
data class DataTablePayload(
    @PrimaryKey(autoGenerate = true)
    @Transient
    var id: Int? = null,

    @ColumnInfo("clientCreationTime")
    @Transient
    var clientCreationTime: Long? = null,

    // this field belongs to database table only for saving the
    // data table string;
    @ColumnInfo("dataTableString")
    @Transient
    var dataTableString: String? = null,

    @ColumnInfo("registeredTableName")
    var registeredTableName: String? = null,

    var data: HashMap<String, @RawValue Any>? = null,
) : MifosBaseModel(), Parcelable
