package com.mifos.feature.loan.loan_transaction

import com.mifos.core.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class LoanTransactionsUiState {

    data object ShowProgressBar : LoanTransactionsUiState()

    data class ShowFetchingError(val message: String) : LoanTransactionsUiState()

    data class ShowLoanTransaction(val loanWithAssociations: LoanWithAssociations) :
        LoanTransactionsUiState()
}
