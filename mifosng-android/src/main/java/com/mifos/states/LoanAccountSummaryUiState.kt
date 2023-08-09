package com.mifos.states

import com.mifos.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class LoanAccountSummaryUiState {

    object ShowProgressbar : LoanAccountSummaryUiState()

    data class ShowFetchingError(val message: String) : LoanAccountSummaryUiState()

    data class ShowLoanById(val loanWithAssociations: LoanWithAssociations) :
        LoanAccountSummaryUiState()
}