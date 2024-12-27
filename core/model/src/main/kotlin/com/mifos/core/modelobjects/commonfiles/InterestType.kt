/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.commonfiles

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by rajan on 13/3/16.
 */
@Parcelize
data class InterestType(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null,
) : Parcelable
