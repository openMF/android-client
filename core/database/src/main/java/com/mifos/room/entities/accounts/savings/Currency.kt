/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "SavingAccountCurrency")
@Parcelize
data class Currency(
    @PrimaryKey
    var code: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "decimalPlaces")
    var decimalPlaces: Int? = null,

    @ColumnInfo(name = "inMultiplesOf")
    var inMultiplesOf: Int? = null,

    @ColumnInfo(name = "displaySymbol")
    var displaySymbol: String? = null,

    @ColumnInfo(name = "nameCode")
    var nameCode: String? = null,

    @ColumnInfo(name = "displayLabel")
    var displayLabel: String? = null,
) : Parcelable
