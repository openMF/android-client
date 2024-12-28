/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import com.mifos.core.`object`.account.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanRepaymentUiState {

    data object ShowProgressbar : LoanRepaymentUiState()

    data class ShowError(val message: Int) : LoanRepaymentUiState()

    data class ShowLoanRepayTemplate(val loanRepaymentTemplate: LoanRepaymentTemplate) :
        LoanRepaymentUiState()

    data class ShowPaymentSubmittedSuccessfully(val loanRepaymentResponse: LoanRepaymentResponse?) :
        LoanRepaymentUiState()

    data object ShowLoanRepaymentExistInDatabase : LoanRepaymentUiState()

    data object ShowLoanRepaymentDoesNotExistInDatabase : LoanRepaymentUiState()
}
