/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 15/12/16.
 */
@Parcelize
data class Address(
    var addressTypeId: Int = -1,

    var isActive: Boolean = false,

    var addressLine1: String = "",

    var addressLine2: String = "",

    var addressLine3: String = "",

    var city: String = "",

    var stateProvinceId: Int = -1,

    var countryId: Int = -1,

    var postalCode: String = "",
) : Parcelable
