/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/07/16.
 */
@Parcelize
@Entity("Currency")
data class Currency(
    @PrimaryKey
    var code: String? = null,

    @ColumnInfo("name")
    var name: String? = null,

    @ColumnInfo("decimalPlaces")
    var decimalPlaces: Int? = null,

    @ColumnInfo("inMultiplesOf")
    var inMultiplesOf: Int? = null,

    @ColumnInfo("displaySymbol")
    var displaySymbol: String? = null,

    @ColumnInfo("nameCode")
    var nameCode: String? = null,

    @ColumnInfo("displayLabel")
    var displayLabel: String? = null,
) : MifosBaseModel(), Parcelable
