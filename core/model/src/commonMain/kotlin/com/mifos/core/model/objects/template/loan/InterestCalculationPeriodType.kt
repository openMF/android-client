/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.loan

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */

@Parcelize
data class InterestCalculationPeriodType(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null,
) : Parcelable
