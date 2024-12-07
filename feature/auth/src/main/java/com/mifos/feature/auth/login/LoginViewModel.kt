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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Network
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.model.getInstanceUrl
import com.mifos.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.apimanager.BaseApiManager
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefManager: PrefManager,
    private val usernameValidationUseCase: UsernameValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val baseApiManager: BaseApiManager,
    private val loginUseCase: com.mifos.core.domain.useCases.LoginUseCase,
) :
    ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val loginUiState = _loginUiState.asStateFlow()

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
        // Updating Services
        baseApiManager.createService(
            username,
            password,
            prefManager.getServerConfig.getInstanceUrl(),
            prefManager.getServerConfig.tenant,
            true,
        )
        if (Network.isOnline(context)) {
            login(username, password)
        } else {
            _loginUiState.value =
                LoginUiState.ShowError(R.string.feature_auth_error_not_connected_internet)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(username, password).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _loginUiState.value =
                            LoginUiState.ShowError(R.string.feature_auth_error_login_failed)
                    }

                    is Resource.Loading -> {
                        _loginUiState.value = LoginUiState.ShowProgress
                    }

                    is Resource.Success -> {
                        result.data?.let { onLoginSuccessful(it, username, password) }
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
        // Saving username password
        prefManager.usernamePassword = Pair(username, password)
        // Saving userID
        prefManager.setUserId(user.userId!!.toInt())
        // Saving user's token
        prefManager.saveToken("Basic " + user.base64EncodedAuthenticationKey)
        // Saving user
        prefManager.savePostAuthenticationResponse(user)

        if (prefManager.getPassCodeStatus()) {
            _loginUiState.value = LoginUiState.HomeActivityIntent
        } else {
            _loginUiState.value = LoginUiState.PassCodeActivityIntent
        }
    }
}
