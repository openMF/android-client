/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.dto.loans.template

import kotlinx.serialization.Serializable

@Serializable
data class LoanChargeOffTemplateDto(
    val chargeOffReasonOptions: List<ChargeOffReasonOptionDto> = emptyList(),
)

@Serializable
data class ChargeOffReasonOptionDto(
    val id: Int,
    val name: String,
    val position: Int,
    val description: String,
    val active: Boolean,
    val mandatory: Boolean,
)
