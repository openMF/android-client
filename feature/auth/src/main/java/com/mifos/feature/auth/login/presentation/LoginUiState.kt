package com.mifos.feature.auth.login.presentation

/**
 * Created by Aditya Gupta on 06/08/23.
 */

sealed class LoginUiState {

    data object Empty : LoginUiState()

    data object ShowProgress : LoginUiState()

    data class ShowError(val message: Int) : LoginUiState()

    data class ShowValidationError(val usernameError: Int? = null, val passwordError: Int? = null) :
        LoginUiState()

    data object HomeActivityIntent : LoginUiState()

    data object PassCodeActivityIntent : LoginUiState()

}