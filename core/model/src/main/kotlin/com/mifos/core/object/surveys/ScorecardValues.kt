/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.`object`.surveys

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@Parcelize
data class ScorecardValues(
    var questionId: Int? = null,

    var responseId: Int? = null,

    var value: Int? = null,
) : Parcelable
