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

import com.mifos.core.model.entity.accounts.loan.LoanForAssignOfficer
import com.mifos.core.model.entity.accounts.loan.StaffOption

internal sealed interface AssignLoanOfficerUiState {
    data object Loading : AssignLoanOfficerUiState

    data class Error(
        val message: String?,
    ) : AssignLoanOfficerUiState

    data class Content(
        val loan: LoanForAssignOfficer,
        val officers: List<StaffOption> = emptyList(),
        val selectedOfficerIndex: Int = -1,
        val officerShowError: Boolean = false,
        val assignmentDateMillis: Long,
        val submitInProgress: Boolean = false,
    ) : AssignLoanOfficerUiState
}

internal sealed interface AssignLoanOfficerEffect {
    data class ShowMessage(
        val message: String,
    ) : AssignLoanOfficerEffect

    data object NavigateBack : AssignLoanOfficerEffect
}

internal sealed interface AssignLoanOfficerAction {
    data object LoadLoan : AssignLoanOfficerAction
    data class LoadOfficers(val officeId: Int) : AssignLoanOfficerAction
    data class SelectOfficer(val index: Int) : AssignLoanOfficerAction
    data class UpdateAssignmentDate(val millis: Long) : AssignLoanOfficerAction
    data object Submit : AssignLoanOfficerAction
}
