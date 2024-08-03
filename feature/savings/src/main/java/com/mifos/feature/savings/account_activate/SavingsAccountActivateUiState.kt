package com.mifos.feature.savings.account_activate

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SavingsAccountActivateUiState {

    data object Initial : SavingsAccountActivateUiState()

    data object ShowProgressbar : SavingsAccountActivateUiState()

    data class ShowError(val message: String) : SavingsAccountActivateUiState()

    data class ShowSavingAccountActivatedSuccessfully(val genericResponse: GenericResponse) :
        SavingsAccountActivateUiState()
}