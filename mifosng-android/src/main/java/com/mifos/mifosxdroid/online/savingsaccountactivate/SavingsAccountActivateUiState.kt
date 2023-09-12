package com.mifos.mifosxdroid.online.savingsaccountactivate

import com.mifos.api.GenericResponse

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SavingsAccountActivateUiState {

    object ShowProgressbar : SavingsAccountActivateUiState()

    data class ShowError(val message: String) : SavingsAccountActivateUiState()

    data class ShowSavingAccountActivatedSuccessfully(val genericResponse: GenericResponse) :
        SavingsAccountActivateUiState()
}