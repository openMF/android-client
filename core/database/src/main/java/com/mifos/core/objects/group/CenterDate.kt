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
 * Created by mayankjindal on 11/07/17.
 */
@Parcelize
@Entity("CenterDate")
data class CenterDate(
    @PrimaryKey
    var centerId: Long = 0,

    @PrimaryKey
    var chargeId: Long = 0,

    @ColumnInfo("day")
    var day: Int = 0,

    @ColumnInfo("month")
    var month: Int = 0,

    @ColumnInfo("year")
    var year: Int = 0,
) : MifosBaseModel(), Parcelable
