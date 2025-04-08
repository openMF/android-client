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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.model.objects.users.User
import com.mifos.feature.auth.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.openapitools.client.models.PostAuthenticationResponse

/**
 * Created by Aditya Gupta on 06/08/23.
 */

class LoginViewModel(
    private val prefManager: UserPreferencesRepository,
//    private val context: Context,
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

    fun validateUserInputs(username: String, password: String) {
        val usernameValidationResult = usernameValidationUseCase(username)
        val passwordValidationResult = passwordValidationUseCase(password)

        val hasError =
            listOf(usernameValidationResult, passwordValidationResult).any { !it.success }

        if (hasError) {
            _loginUiState.value = LoginUiState.ShowValidationError(
                usernameValidationResult.message,
                passwordValidationResult.message,
            )
            return
        }
        viewModelScope.launch {
            setupPrefManger(username, password)
        }
    }

    private fun setupPrefManger(username: String, password: String) {
        Log.d("sdfdf", username + password)
//        if (Network.isOnline(context)) {
        login(username, password)
//        } else {
//        _loginUiState.value =
//            LoginUiState.ShowError(R.string.feature_auth_error_not_connected_internet)
//        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(username, password).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _loginUiState.value =
                            LoginUiState.ShowError(R.string.feature_auth_error_login_failed)
                        Log.e("@@@", "login: ${result.message}")
                    }

                    is Resource.Loading -> {
                        _loginUiState.value = LoginUiState.ShowProgress
                    }

                    is Resource.Success -> {
                        result.data?.let {
                            if (it.userId != null && it.authenticated == true) {
                                onLoginSuccessful(it, username, password)
                            }
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

        if (passcode.value != null) {
            _loginUiState.value = LoginUiState.HomeActivityIntent
        } else {
            _loginUiState.value = LoginUiState.PassCodeActivityIntent
        }
    }
}
