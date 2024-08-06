package com.mifos.feature.loan.loan_account_summary

import com.mifos.core.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class LoanAccountSummaryUiState {

    data object ShowProgressbar : LoanAccountSummaryUiState()

    data class ShowFetchingError(val message: String) : LoanAccountSummaryUiState()

    data class ShowLoanById(val loanWithAssociations: LoanWithAssociations) : LoanAccountSummaryUiState()
}