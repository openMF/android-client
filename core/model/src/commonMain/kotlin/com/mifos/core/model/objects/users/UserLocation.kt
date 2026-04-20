/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.users

import com.mifos.core.model.utils.DateConstants
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 24/01/17.
 */
@Serializable
@Parcelize
data class UserLocation(
    var staffId: Int? = null,

    var latLng: String? = null,

    var startTime: String? = null,

    var stopTime: String? = null,

    var date: String? = null,

    var startAddress: String? = null,

    var endAddress: String? = null,

    var dateFormat: String? = DateConstants.DATE_FORMAT_WITH_TIME,

    var locale: String? = DateConstants.LOCALE,
) : Parcelable
