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

import kotlinx.serialization.Serializable

@Serializable
data class ShareAccountPayload(
    val clientId: Int,

    val productId: Int? = null,

    val requestedShares: Int,

    val externalId: String? = null,

    val submittedDate: String,

    val minimumActivePeriod: Int? = null,

    val minimumActivePeriodFrequencyType: Int? = null,

    val lockinPeriodFrequency: Int? = null,

    val lockinPeriodFrequencyType: Int? = null,

    val applicationDate: String,

    val allowDividendCalculationForInactiveClients: Boolean,

    val locale: String? = null,

    val dateFormat: String? = null,

    val charges: List<ChargeItem> = emptyList(),

    val savingsAccountId: Int,
)

@Serializable
data class ChargeItem(
    val chargeId: Int? = null,

    val amount: Double? = null,
)
