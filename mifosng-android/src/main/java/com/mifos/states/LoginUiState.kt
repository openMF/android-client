package com.mifos.states

import com.mifos.objects.user.User

/**
 * Created by Aditya Gupta on 06/08/23.
 */

sealed class LoginUiState {

    data class ShowProgress(val state: Boolean) : LoginUiState()

    data class ShowError(val message: String) : LoginUiState()

    data class ShowLoginSuccessful(val user: User) : LoginUiState()

}
