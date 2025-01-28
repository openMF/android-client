/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.account.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Transaction(
    var id: Int? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var type: Type? = null,

    var date: List<Int> = ArrayList(),

    var currency: Currency? = null,

    var paymentDetailData: PaymentDetailData? = null,

    var amount: Double? = null,

    var principalPortion: Double? = null,

    var interestPortion: Double? = null,

    var feeChargesPortion: Double? = null,

    var penaltyChargesPortion: Double? = null,

    var overpaymentPortion: Double? = null,
) : Parcelable
