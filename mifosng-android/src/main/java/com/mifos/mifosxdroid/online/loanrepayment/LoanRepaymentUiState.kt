package com.mifos.mifosxdroid.online.loanrepayment

import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanRepaymentUiState {

    data object ShowProgressbar : LoanRepaymentUiState()

    data class ShowError(val message: Int) : LoanRepaymentUiState()

    data class ShowLoanRepayTemplate(val loanRepaymentTemplate: LoanRepaymentTemplate?) :
        LoanRepaymentUiState()

    data class ShowPaymentSubmittedSuccessfully(val loanRepaymentResponse: LoanRepaymentResponse?) :
        LoanRepaymentUiState()

    data object ShowLoanRepaymentExistInDatabase : LoanRepaymentUiState()

    data object ShowLoanRepaymentDoesNotExistInDatabase : LoanRepaymentUiState()
}