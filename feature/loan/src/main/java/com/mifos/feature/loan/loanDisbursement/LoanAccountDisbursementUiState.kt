/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanAccountDisbursementUiState {

    data object ShowProgressbar : LoanAccountDisbursementUiState()

    data class ShowError(val message: String) : LoanAccountDisbursementUiState()

    data class ShowLoanTransactionTemplate(val loanTransactionTemplate: LoanTransactionTemplate) :
        LoanAccountDisbursementUiState()

    data class ShowDisburseLoanSuccessfully(val genericResponse: GenericResponse?) :
        LoanAccountDisbursementUiState()
}
