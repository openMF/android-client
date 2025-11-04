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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareTemplate(
    @SerialName("clientId")
    val clientId: Int,

    @SerialName(value = "clientName")
    val clientName: String,

    @SerialName("productOptions")
    val productOptions: List<ProductOption> = emptyList(),

    @SerialName("savingsAccountOptions")
    val savingsAccountOptions: List<SavingsAccountOption>? = emptyList(),

    @SerialName("lockinPeriodFrequencyTypeOptions")
    val lockinPeriodFrequencyTypeOptions: List<FrequencyTypeOption>? = emptyList(),

    @SerialName("minimumActivePeriodFrequencyTypeOptions")
    val minimumActivePeriodFrequencyTypeOptions: List<FrequencyTypeOption>? = emptyList(),
)

@Serializable
data class SavingsAccountOption(
    @SerialName("id")
    val id: Int,

    @SerialName("accountNo")
    val accountNo: String,

    @SerialName("productName")
    val productName: String? = null,

    @SerialName("productId")
    val productId: Int? = null,
)

@Serializable
data class FrequencyTypeOption(
    @SerialName("id")
    val id: Int,

    @SerialName("code")
    val code: String,

    @SerialName("value")
    val value: String,
)
