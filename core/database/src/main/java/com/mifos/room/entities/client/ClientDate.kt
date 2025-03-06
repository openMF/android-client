/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "ClientDate",
    primaryKeys = ["clientId", "chargeId"],
)
data class ClientDate(
    val clientId: Long = 0,

    val chargeId: Long = 0,

    val day: Int = 0,

    val month: Int = 0,

    val year: Int = 0,
) : Parcelable
