package com.mifos.feature.loan.loan_approval

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanAccountApprovalUiState {

    data object Initial : LoanAccountApprovalUiState()

    data object ShowProgressbar : LoanAccountApprovalUiState()

    data class ShowLoanApproveFailed(val message: String) : LoanAccountApprovalUiState()

    data class ShowLoanApproveSuccessfully(val genericResponse: GenericResponse) :
        LoanAccountApprovalUiState()
}