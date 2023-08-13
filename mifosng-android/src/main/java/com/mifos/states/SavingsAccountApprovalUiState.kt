package com.mifos.states

import com.mifos.api.GenericResponse

sealed class SavingsAccountApprovalUiState {

    object ShowProgressbar : SavingsAccountApprovalUiState()

    data class ShowError(val message : String) : SavingsAccountApprovalUiState()

    data class ShowSavingAccountApprovedSuccessfully(val genericResponse: GenericResponse) : SavingsAccountApprovalUiState()
}