/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.recurring.charge

import com.mifos.core.model.objects.template.recurring.Currency
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChargeOption(
    val active: Boolean? = null,
    val amount: Double? = null,
    @IgnoredOnParcel val chargeAppliesTo: ChargeAppliesTo? = null,
    @IgnoredOnParcel val chargeCalculationType: ChargeCalculationType? = null,
    @IgnoredOnParcel val chargePaymentMode: ChargePaymentMode? = null,
    @IgnoredOnParcel val chargeTimeType: ChargeTimeType? = null,
    @IgnoredOnParcel val currency: Currency? = null,
    val freeWithdrawal: Boolean? = null,
    val freeWithdrawalChargeFrequency: Int? = null,
    val id: Int? = null,
    val isPaymentType: Boolean? = null,
    val name: String? = null,
    val penalty: Boolean? = null,
    val restartFrequency: Int? = null,
    val restartFrequencyEnum: Int? = null,
) : Parcelable
