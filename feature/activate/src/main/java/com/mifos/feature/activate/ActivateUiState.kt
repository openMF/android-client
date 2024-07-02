package com.mifos.feature.activate

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ActivateUiState {

    data object Initial : ActivateUiState()

    data object Loading : ActivateUiState()

    data class Error(val message: Int) : ActivateUiState()

    data class ActivatedSuccessfully(val message: Int) : ActivateUiState()
}
