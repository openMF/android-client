/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 24/01/17.
 */
@Parcelize
data class UserLocation(
    var userId: Int? = null,

    var latLng: String? = null,

    var startTime: String? = null,

    var stopTime: String? = null,

    var date: String? = null,

    var dateFormat: String? = "dd MMMM yyyy HH:mm",

    var locale: String? = "en",
) : Parcelable
