/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Loan(
    var disbursementAmount: Double = 0.0,

    var interestDue: Double = 0.0,

    var interestPaid: Double = 0.0,

    var loanId: Int = 0,

    var chargesDue: Double = 0.0,

    var totalDue: Double = 0.0,

    var principalDue: Double = 0.0,

    var principalPaid: Double = 0.0,

    var accountId: String? = null,

    var accountStatusId: Int = 0,

    var productShortName: String? = null,

    var productId: Int = 0,

    var currency: Currency? = null,

    var client: Client? = null,

    var isPaymentChanged: String? = null,
) : Parcelable
