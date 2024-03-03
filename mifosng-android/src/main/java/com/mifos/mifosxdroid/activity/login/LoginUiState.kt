package com.mifos.mifosxdroid.activity.login

import org.apache.fineract.client.models.PostAuthenticationResponse

/**
 * Created by Aditya Gupta on 06/08/23.
 */

sealed class LoginUiState {

    data class ShowProgress(val state: Boolean) : LoginUiState()

    data class ShowError(val message: String) : LoginUiState()

    data class ShowLoginSuccessful(val user: PostAuthenticationResponse) : LoginUiState()

}
