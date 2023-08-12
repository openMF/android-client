package com.mifos.states

import com.mifos.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class LoanTransactionsUiState {

    object ShowProgressBar : LoanTransactionsUiState()

    data class ShowFetchingError(val message: String) : LoanTransactionsUiState()

    data class ShowLoanTransaction(val loanWithAssociations: LoanWithAssociations) :
        LoanTransactionsUiState()
}
