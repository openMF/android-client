/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model.share

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductOption(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("shortName")
    val shortName: String,

    @SerialName("totalShares")
    val totalShares: Int,
)
