/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.organisation

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/07/14.
 */
@Parcelize
@Entity("Office")
data class Office(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("externalId")
    var externalId: String? = null,

    @ColumnInfo("name")
    var name: String? = null,

    @ColumnInfo("nameDecorated")
    var nameDecorated: String? = null,

    @Embedded
    @ColumnInfo("officeOpeningDate")
    var officeOpeningDate: OfficeOpeningDate? = null,

    var openingDate: List<Int?> = ArrayList(),
) : MifosBaseModel(), Parcelable
