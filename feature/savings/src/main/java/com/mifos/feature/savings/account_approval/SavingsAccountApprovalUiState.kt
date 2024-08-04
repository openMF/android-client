package com.mifos.feature.savings.account_approval

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SavingsAccountApprovalUiState {

    data object Initial : SavingsAccountApprovalUiState()

    data object ShowProgressbar : SavingsAccountApprovalUiState()

    data class ShowError(val message: String) : SavingsAccountApprovalUiState()

    data class ShowSavingAccountApprovedSuccessfully(val genericResponse: GenericResponse) :
        SavingsAccountApprovalUiState()
}