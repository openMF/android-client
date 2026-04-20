/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.model.share

import com.mifos.core.model.objects.template.client.ChargeAppliesTo
import com.mifos.core.model.objects.template.client.ChargeCalculationType
import com.mifos.core.model.objects.template.client.ChargePaymentMode
import com.mifos.core.model.objects.template.client.ChargeTimeType
import com.mifos.core.model.objects.template.client.Currency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareTemplate(
    val clientId: Int,

    val clientName: String,

    val currency: Currency? = null,

    val currentMarketPrice: Double? = null,

    val productOptions: List<ProductOption> = emptyList(),

    val chargeOptions: List<ChargeOptions> = emptyList(),

    @SerialName("clientSavingsAccounts")
    val savingsAccountOptions: List<SavingsAccountOption>? = emptyList(),

    val lockinPeriodFrequencyTypeOptions: List<FrequencyTypeOption>? = emptyList(),

    val minimumActivePeriodFrequencyTypeOptions: List<FrequencyTypeOption>? = emptyList(),
)

@Serializable
data class SavingsAccountOption(
    val id: Int,

    val accountNo: String,

    val savingsProductName: String? = null,

    val savingsProductId: Int? = null,
)

@Serializable
data class FrequencyTypeOption(
    val id: Int,

    val code: String,

    val value: String,
)

@Serializable
data class ChargeOptions(
    val id: Int? = null,
    val name: String? = null,
    val active: Boolean? = null,
    val penalty: Boolean? = null,
    val currency: Currency? = null,
    val amount: Double? = null,
    val chargeTimeType: ChargeTimeType? = null,
    val chargeAppliesTo: ChargeAppliesTo? = null,
    val chargeCalculationType: ChargeCalculationType? = null,
    val chargePaymentMode: ChargePaymentMode? = null,
)
