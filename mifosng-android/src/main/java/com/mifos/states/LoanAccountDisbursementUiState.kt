package com.mifos.states

import com.mifos.api.GenericResponse
import com.mifos.objects.templates.loans.LoanTransactionTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanAccountDisbursementUiState {

    object ShowProgressbar : LoanAccountDisbursementUiState()

    data class ShowError(val message: String) : LoanAccountDisbursementUiState()

    data class ShowLoanTransactionTemplate(val loanTransactionTemplate: LoanTransactionTemplate) :
        LoanAccountDisbursementUiState()

    data class ShowDisburseLoanSuccessfully(val genericResponse: GenericResponse?) :
        LoanAccountDisbursementUiState()
}
