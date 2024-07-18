package com.mifos.feature.loan.loan_account

import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class LoanAccountUiState {

    data object Loading : LoanAccountUiState()

    data class AllLoan(val productLoans: List<LoanProducts>) : LoanAccountUiState()

    data class Error(val message: Int) : LoanAccountUiState()

    data object LoanAccountCreatedSuccessfully : LoanAccountUiState()
}