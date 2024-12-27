/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.template.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val code: String,
    val name: String,
    val decimalPlaces: Double,
    val inMultiplesOf: Int,
    val displaySymbol: String,
    val nameCode: String,
    val displayLabel: String,
) : Parcelable
