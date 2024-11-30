/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanApproval

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanAccountApprovalUiState {

    data object Initial : LoanAccountApprovalUiState()

    data object ShowProgressbar : LoanAccountApprovalUiState()

    data class ShowLoanApproveFailed(val message: String) : LoanAccountApprovalUiState()

    data class ShowLoanApproveSuccessfully(val genericResponse: GenericResponse) :
        LoanAccountApprovalUiState()
}
