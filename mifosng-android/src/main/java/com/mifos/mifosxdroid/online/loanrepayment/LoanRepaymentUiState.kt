package com.mifos.mifosxdroid.online.loanrepayment

import com.mifos.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.objects.templates.loans.LoanRepaymentTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanRepaymentUiState {

    object ShowProgressbar : LoanRepaymentUiState()

    data class ShowError(val message: Int) : LoanRepaymentUiState()

    data class ShowLoanRepayTemplate(val loanRepaymentTemplate: LoanRepaymentTemplate?) :
        LoanRepaymentUiState()

    data class ShowPaymentSubmittedSuccessfully(val loanRepaymentResponse: LoanRepaymentResponse?) :
        LoanRepaymentUiState()

    object ShowLoanRepaymentExistInDatabase : LoanRepaymentUiState()

    object ShowLoanRepaymentDoesNotExistInDatabase : LoanRepaymentUiState()
}