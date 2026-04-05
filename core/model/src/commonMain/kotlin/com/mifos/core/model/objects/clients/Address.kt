/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
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
    val addressTypeId: Int? = null,

    val isActive: Boolean = false,

    val addressLine1: String? = null,

    val addressLine2: String? = null,

    val addressLine3: String? = null,

    val city: String? = null,

    val stateProvinceId: Int? = null,

    val countryId: Int? = null,

    val postalCode: String? = null,
) : Parcelable
