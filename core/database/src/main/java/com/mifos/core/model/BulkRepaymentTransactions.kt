/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BulkRepaymentTransactions(

    var loanId: Int = 0,

    var transactionAmount: Double = 0.0,

    // Optional fields
    var accountNumber: String? = null,

    var bankNumber: String? = null,

    var checkNumber: String? = null,

    var paymentTypeId: Int? = null,

    var receiptNumber: String? = null,

    var routingCode: String? = null,
) : Parcelable
