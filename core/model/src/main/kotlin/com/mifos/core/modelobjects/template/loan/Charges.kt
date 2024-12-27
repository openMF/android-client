/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.template.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Charges(
    var chargeId: Int? = null,

    var name: String? = null,

    var chargeTimeType: ChargeTimeType? = null,

    var chargeCalculationType: ChargeCalculationType? = null,

    var currency: Currency? = null,

    var amount: Double? = null,

    var amountPaid: Double? = null,

    var amountWaived: Double? = null,

    var amountWrittenOff: Double? = null,

    var amountOutstanding: Double? = null,

    var amountOrPercentage: Double? = null,

    var penalty: Boolean? = null,

    var chargePaymentMode: ChargePaymentMode? = null,

    var paid: Boolean? = null,

    var waived: Boolean? = null,

    var chargePayable: Boolean? = null,
) : Parcelable
