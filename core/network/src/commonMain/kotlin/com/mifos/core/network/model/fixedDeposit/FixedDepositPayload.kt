/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.model.fixedDeposit

import com.mifos.core.model.objects.payloads.ChargeItem
import kotlinx.serialization.Serializable

@Serializable
data class FixedDepositPayload(
    val clientId: Int? = null,
    val productId: Int? = null,
    val fieldOfficerId: Int? = null,

    val locale: String? = null,
    val dateFormat: String? = null,
    val submittedOnDate: String? = null,

    val accountNo: String? = null,
    val externalId: String? = null,
    val transferToSavingsId: Int? = null,
    val linkAccountId: Int? = null,

    val interestCompoundingPeriodType: Int? = null,
    val interestPostingPeriodType: Int? = null,
    val interestCalculationType: Int? = null,
    val interestCalculationDaysInYearType: Int? = null,

    val depositAmount: Double? = null,
    val depositPeriod: Int? = null,
    val depositPeriodFrequencyId: Int? = null,

    val lockinPeriodFrequency: Int? = null,
    val lockinPeriodFrequencyType: Int? = null,

    val charges: List<ChargeItem> = emptyList(),

    val transferInterestToSavings: Boolean = false,

    val preClosurePenalApplicable: Boolean = false,
    val preClosurePenalInterest: Double? = null,
    val preClosurePenalInterestOnTypeId: Int? = null,

    val maturityInstructionId: Int? = null,
)
