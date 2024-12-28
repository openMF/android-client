/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.`object`.groups

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@Parcelize
data class CenterInfo(
    var activeClients: Int? = null,

    var activeLoans: Int? = null,

    var activeClientLoans: Int? = null,

    var activeGroupLoans: Int? = null,

    var activeBorrowers: Int? = null,

    var activeClientBorrowers: Int? = null,

    var activeGroupBorrowers: Int? = null,

    var overdueLoans: Int? = null,

    var overdueClientLoans: Int? = null,

    var overdueGroupLoans: Int? = null,
) : Parcelable
