/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.mifoserror

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Errors(
    var developerMessage: String = "",

    var defaultUserMessage: String = "",

    var userMessageGlobalisationCode: String = "",

    var parameterName: String? = null,
) : Parcelable
