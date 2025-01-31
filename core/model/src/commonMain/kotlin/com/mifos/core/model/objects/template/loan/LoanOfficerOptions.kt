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
data class LoanOfficerOptions(
    var id: Int? = null,

    var firstname: String? = null,

    var lastname: String? = null,

    var displayName: String? = null,

    var mobileNo: String? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var isLoanOfficer: Boolean? = null,

    var isActive: Boolean? = null,

    var joiningDate: List<Int>? = null,
) : Parcelable
