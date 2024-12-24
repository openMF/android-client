/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 1/22/2016.
 */
@Parcelize
@Entity(tableName = "CenterPayload")
data class CenterPayload(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "error_message")
    @Transient
    var errorMessage: String? = null,

    @ColumnInfo(name = "date_format")
    var dateFormat: String? = null,

    @ColumnInfo(name = "locale")
    var locale: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "office_id")
    var officeId: Int? = null,

    @ColumnInfo(name = "is_active")
    var active: Boolean = false,

    @ColumnInfo(name = "activation_date")
    var activationDate: String? = null,
) : MifosBaseModel(), Parcelable
