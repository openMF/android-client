/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.`object`.checkerinboxtask

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RescheduleReasonCodeValue(
    var id: Int,
    var name: String,
    var active: Boolean,
    var mandatory: Boolean,
) : Parcelable
