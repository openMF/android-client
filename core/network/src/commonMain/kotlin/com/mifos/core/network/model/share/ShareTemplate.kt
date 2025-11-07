/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model.share

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
