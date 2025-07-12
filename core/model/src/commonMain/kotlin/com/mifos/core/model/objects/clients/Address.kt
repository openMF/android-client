/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.clients

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 15/12/16.
 */
@Parcelize
@Serializable
data class Address(
    val addressTypeId: Int = -1,

    val isActive: Boolean = false,

    val addressLine1: String = "",

    val addressLine2: String = "",

    val addressLine3: String = "",

    val city: String = "",

    val stateProvinceId: Int = -1,

    val countryId: Int = -1,

    val postalCode: String = "",
) : Parcelable
