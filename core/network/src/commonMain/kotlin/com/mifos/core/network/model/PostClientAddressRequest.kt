/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PostClientAddressRequest(
    val addressLine1: String = "",

    val addressLine2: String = "",

    val addressLine3: String = "",

    val city: String = "",

    val stateProvinceId: Int = -1,

    val countryId: Int = -1,

    val postalCode: String = "",
) : Parcelable
