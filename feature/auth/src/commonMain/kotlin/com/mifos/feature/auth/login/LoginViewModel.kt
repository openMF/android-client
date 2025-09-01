/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
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
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.model.objects.users.User
import com.mifos.core.network.model.PostAuthenticationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 06/08/23.
 */

class LoginViewModel(
    private val prefManager: UserPreferencesRepository,
    private val usernameValidationUseCase: UsernameValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState = _loginUiState.asStateFlow()

    private val passcode: StateFlow<String?> = prefManager.settingsInfo
        .map { it.passcode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

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
        viewModelScope.launch {
            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        viewModelScope.launch {
            loginUseCase(username, password).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _loginUiState.value =
                            LoginUiState.ShowError(Res.string.feature_auth_error_login_failed)
                        Logger.d("@@@", Throwable("login: ${result.data}"))
                    }

                    is DataState.Loading -> {
                        _loginUiState.value = LoginUiState.ShowProgress
                    }

                    is DataState.Success -> {
                        if (result.data.authenticated == true) {
                            onLoginSuccessful(result.data, username, password)
                        } else {
                            _loginUiState.value =
                                LoginUiState.ShowError(Res.string.feature_auth_error_login_failed)

                            Logger.d("@@@", Throwable("login: ${result.data}"))
                        }
                    }
                }
            }
        }
    }

    private fun onLoginSuccessful(
        user: PostAuthenticationResponse,
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            prefManager.updateUser(
                User(
                    username = username,
                    password = password,
                    userId = user.userId!!.toLong(),
                    base64EncodedAuthenticationKey = user.base64EncodedAuthenticationKey,
                    isAuthenticated = user.authenticated ?: false,
                    officeId = user.officeId!!,
                    officeName = user.officeName,
                    permissions = user.permissions!!,
                ),
            )
        }

        _loginUiState.value = LoginUiState.HomeActivityIntent

//        if (passcode.value != null) {
//        TODO() navigate to passcode screen
//        } else {
//        TODO() navigate to home screen
//        }
    }
}
