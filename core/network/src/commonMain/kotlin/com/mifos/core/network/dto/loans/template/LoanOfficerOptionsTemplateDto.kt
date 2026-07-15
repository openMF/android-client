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
data class LoanOfficerOptionsTemplateDto(
    val loanOfficerOptions: List<LoanOfficerOptionDto> = emptyList(),
)

@Serializable
data class LoanOfficerOptionDto(
    val id: Int,
    val firstname: String? = null,
    val lastname: String? = null,
    val displayName: String? = null,
    val mobileNo: String? = null,
    val officeId: Int? = null,
    val officeName: String? = null,
    val isLoanOfficer: Boolean? = null,
    val isActive: Boolean? = null,
)
