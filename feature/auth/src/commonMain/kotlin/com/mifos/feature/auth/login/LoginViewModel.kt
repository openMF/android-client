/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.auth.login

import androidclient.feature.auth.generated.resources.Res
import androidclient.feature.auth.generated.resources.feature_auth_error_login_failed
import androidclient.feature.auth.generated.resources.feature_auth_error_password_length
import androidclient.feature.auth.generated.resources.feature_auth_error_username_length
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.store.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.model.objects.users.User
import com.mifos.core.network.model.PostAuthenticationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(
    private val prefManager: UserPreferencesRepository,
    private val usernameValidationUseCase: UsernameValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState = _loginUiState.asStateFlow()

    private val submit = viewModelScope.submitHandler<PostAuthenticationResponse>()
    private var pendingCredentials: Pair<String, String>? = null

    init {
        submit.state
            .onEach { state ->
                when (state) {
                    is SubmitState.Idle -> Unit
                    is SubmitState.Submitting -> {
                        _loginUiState.value = LoginUiState.ShowProgress
                    }
                    is SubmitState.Submitted -> {
                        val (username, password) = pendingCredentials ?: return@onEach
                        persistUserAndContinue(state.result, username, password)
                    }
                    is SubmitState.Failed -> {
                        _loginUiState.value =
                            LoginUiState.ShowError(Res.string.feature_auth_error_login_failed)
                        Logger.d("@@@", state.error)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    suspend fun validateUserInputs(username: String, password: String) {
        val usernameValidationResult = usernameValidationUseCase(username)
        val passwordValidationResult = passwordValidationUseCase(password)

        val hasError =
            listOf(usernameValidationResult, passwordValidationResult).any { !it.success }

        if (hasError) {
            _loginUiState.value = LoginUiState.ShowValidationError(
                Res.string.feature_auth_error_username_length,
                Res.string.feature_auth_error_password_length,
            )
            return
        }

        pendingCredentials = username to password
        submit.submit {
            val terminal = loginUseCase(username, password)
                .first { it !is DataState.Loading }
            when (terminal) {
                is DataState.Error -> throw terminal.exception
                is DataState.Success -> {
                    val response = terminal.data
                    if (response.authenticated == true) response
                    else throw LoginRejectedException(response)
                }
                is DataState.Loading -> error("Unreachable: filtered above")
            }
        }
    }

    private suspend fun persistUserAndContinue(
        user: PostAuthenticationResponse,
        username: String,
        password: String,
    ) {
        prefManager.updateUser(
            User(
                username = username,
                password = password,
                userId = user.userId!!,
                base64EncodedAuthenticationKey = user.base64EncodedAuthenticationKey,
                isAuthenticated = user.authenticated ?: false,
                officeId = user.officeId!!,
                officeName = user.officeName,
                permissions = user.permissions!!,
            ),
        )
        _loginUiState.value = LoginUiState.PassCodeActivityIntent
    }
}

private class LoginRejectedException(val response: PostAuthenticationResponse) :
    RuntimeException("Login rejected: authenticated=${response.authenticated}")
