package com.mifos.mifosxdroid.online.loanaccount

import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class LoanAccountUiState {

    data object ShowProgressbar : LoanAccountUiState()

    data class ShowMessage(val message: Int) : LoanAccountUiState()

    data class ShowAllLoan(val productLoans: List<LoanProducts>) : LoanAccountUiState()

    data class ShowLoanAccountTemplate(val loanTemplate: LoanTemplate) : LoanAccountUiState()

    data class ShowFetchingError(val message: String?) : LoanAccountUiState()

    data class ShowLoanAccountCreatedSuccessfully(val loans: Loans?) : LoanAccountUiState()
}