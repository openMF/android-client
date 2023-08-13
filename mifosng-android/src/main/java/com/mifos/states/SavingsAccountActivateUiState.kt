package com.mifos.states

import com.mifos.api.GenericResponse

sealed class SavingsAccountActivateUiState {

    object ShowProgressbar : SavingsAccountActivateUiState()

    data class ShowError(val message : String) : SavingsAccountActivateUiState()

    data class ShowSavingAccountActivatedSuccessfully(val genericResponse: GenericResponse) : SavingsAccountActivateUiState()
}