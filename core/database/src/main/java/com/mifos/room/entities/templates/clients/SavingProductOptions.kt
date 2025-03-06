/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.clients

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientTemplateSavingProductsOptions")
data class SavingProductOptions(
    @PrimaryKey
    var id: Int = 0,

    val name: String = "",

    val withdrawalFeeForTransfers: Boolean = false,

    val allowOverdraft: Boolean = false,
) : Parcelable
