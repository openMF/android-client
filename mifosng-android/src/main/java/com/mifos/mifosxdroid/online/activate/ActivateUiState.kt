package com.mifos.mifosxdroid.online.activate

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ActivateUiState {

    data class ShowProgressbar(val state: Boolean) : ActivateUiState()

    data class ShowError(val message: String) : ActivateUiState()

    data class ShowActivatedSuccessfully(val message: Int) : ActivateUiState()
}
