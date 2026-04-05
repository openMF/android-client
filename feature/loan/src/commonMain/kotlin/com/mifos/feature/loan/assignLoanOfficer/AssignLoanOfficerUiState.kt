/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.assignLoanOfficer

import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity

internal data class AssignLoanOfficerUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val loan: LoanWithAssociationsEntity? = null,
    val officers: List<StaffEntity> = emptyList(),
    val selectedOfficerIndex: Int = -1,
    val officerShowError: Boolean = false,
    val assignmentDateMillis: Long = 0L,
    val submitInProgress: Boolean = false,
    val submitError: String? = null,
    val completed: Boolean = false,
)
