package com.mifos.mifosxdroid.online.loanaccountapproval

import com.mifos.api.GenericResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanAccountApprovalUiState {

    object ShowProgressbar : LoanAccountApprovalUiState()

    data class ShowLoanApproveFailed(val message: String) : LoanAccountApprovalUiState()

    data class ShowLoanApproveSuccessfully(val genericResponse: GenericResponse) :
        LoanAccountApprovalUiState()
}