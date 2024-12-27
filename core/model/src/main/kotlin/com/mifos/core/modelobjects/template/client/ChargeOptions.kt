/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.template.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */

@Parcelize
data class ChargeOptions(
    val id: Int,
    val name: String,
    val active: Boolean,
    val penalty: Boolean,
    val currency: Currency,
    val amount: Double,
    val chargeTimeType: ChargeTimeType,
    val chargeAppliesTo: ChargeAppliesTo,
    val chargeCalculationType: ChargeCalculationType,
    val chargePaymentMode: ChargePaymentMode,
    val incomeOrLiabilityAccount: IncomeOrLiabilityAccount,
) : Parcelable
