package com.mifos.feature.loan.loan_disbursement

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanAccountDisbursementUiState {

    data object ShowProgressbar : LoanAccountDisbursementUiState()

    data class ShowError(val message: String) : LoanAccountDisbursementUiState()

    data class ShowLoanTransactionTemplate(val loanTransactionTemplate: LoanTransactionTemplate) :
        LoanAccountDisbursementUiState()

    data class ShowDisburseLoanSuccessfully(val genericResponse: GenericResponse?) :
        LoanAccountDisbursementUiState()
}
