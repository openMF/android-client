/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.group

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 1/22/2016.
 */
@Parcelize
@Entity("GroupPayload")
data class GroupPayload(
    @PrimaryKey(autoGenerate = true)
    @Transient
    var id: Int = 0,

    @ColumnInfo("errorMessage")
    @Transient
    var errorMessage: String? = null,

    @ColumnInfo("officeId")
    var officeId: Int = 0,

    @ColumnInfo("active")
    var active: Boolean = false,

    @ColumnInfo("activationDate")
    var activationDate: String? = null,

    @ColumnInfo("submittedOnDate")
    var submittedOnDate: String? = null,

    @ColumnInfo("externalId")
    var externalId: String? = null,

    @ColumnInfo("name")
    var name: String? = null,

    @ColumnInfo("locale")
    var locale: String? = null,

    @ColumnInfo("dateFormat")
    var dateFormat: String? = null,
) : MifosBaseModel(), Parcelable
