/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize

@Parcelize
class LoanApproval(
    var approvedOnDate: String? = null,

    var approvedLoanAmount: String? = null,

    var expectedDisbursementDate: String? = null,

    var note: String? = null,

    var locale: String = "en",

    var dateFormat: String = "dd MMMM yyyy",
) : Parcelable
