/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.client

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/07/16.
 */
@Parcelize
@Table(database = MifosDatabase::class, name = "ClientChargeCurrency")
@ModelContainer
data class Currency(
    @PrimaryKey
    var code: String? = null,

    @Column
    var name: String? = null,

    @Column
    var decimalPlaces: Int? = null,

    @Column
    var inMultiplesOf: Int? = null,

    @Column
    var displaySymbol: String? = null,

    @Column
    var nameCode: String? = null,

    @Column
    var displayLabel: String? = null,
) : MifosBaseModel(), Parcelable
