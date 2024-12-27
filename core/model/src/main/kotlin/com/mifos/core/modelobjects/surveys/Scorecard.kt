/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.surveys

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@Parcelize
data class Scorecard(
    var userId: Int = 0,

    var clientId: Int = 0,

    var createdOn: Date? = null,

    var scorecardValues: List<ScorecardValues>? = null,
) : Parcelable
