/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("SavingAccountCurrency")
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
