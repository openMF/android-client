/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.client.ChargeCalculationTypeEntity
import com.mifos.room.entities.client.ChargeTimeTypeEntity
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Charge(
    val id: Int? = null,

    val chargeId: Int? = null,

    val accountId: Int? = null,

    val name: String? = null,

    @IgnoredOnParcel
    val chargeTimeType: ChargeTimeTypeEntity? = null,

    @IgnoredOnParcel
    val chargeCalculationType: ChargeCalculationTypeEntity? = null,

    val percentage: Int? = null,

    val amountPercentageAppliedTo: Int? = null,

    val currency: SavingAccountCurrencyEntity? = null,

    val amount: Double? = null,

    val amountPaid: Double? = null,

    val amountWaived: Double? = null,

    val amountWrittenOff: Double? = null,

    val amountOutstanding: Double? = null,

    val amountOrPercentage: Double? = null,

    val penalty: Boolean? = null,

    val additionalProperties: MutableMap<String, String> = HashMap(),
) : Parcelable {

    fun setAdditionalProperty(name: String, value: String) {
        additionalProperties[name] = value
    }
}
