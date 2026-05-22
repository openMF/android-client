/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.auth.ui

import androidclient.feature.auth.generated.resources.Res
import androidclient.feature.auth.generated.resources.feature_auth_error_login_failed
import androidclient.feature.auth.generated.resources.feature_auth_error_password_length
import androidclient.feature.auth.generated.resources.feature_auth_error_username_length
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.data.auth.LoginRepository
import com.mifos.core.data.store.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.model.objects.users.User
import com.mifos.core.model.network.PostAuthenticationResponse
import com.mifos.core.ui.store.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginViewModel(
    private val prefManager: UserPreferencesRepository,
    private val usernameValidationUseCase: UsernameValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val loginRepository: LoginRepository,
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(initialState = LoginState()) {

    private val submit = viewModelScope.submitHandler<PostAuthenticationResponse>()

    val submitState: StateFlow<SubmitState<PostAuthenticationResponse>> = submit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    private var pendingCredentials: Pair<String, String>? = null

    init {
        submit.state
            .onEach { state ->
                when (state) {
                    is SubmitState.Idle, is SubmitState.Submitting -> Unit

                    is SubmitState.Submitted -> {
                        val (username, password) = pendingCredentials ?: return@onEach
                        val response = state.result
                        if (response.authenticated == true) {
                            persistUser(response, username, password)
                            sendEvent(LoginEvent.NavigateToPasscode)
                        } else {
                            sendEvent(LoginEvent.ShowError(Res.string.feature_auth_error_login_failed))
                            Logger.d("Login", Throwable("login response not authenticated: ${response.authenticated}"))
                        }
                    }

                    is SubmitState.Failed -> {
                        sendEvent(LoginEvent.ShowError(Res.string.feature_auth_error_login_failed))
                        Logger.d("Login", state.error)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.Submit -> viewModelScope.launch { validateAndSubmit(action.username, action.password) }
            is LoginAction.DismissError -> submit.reset()
        }
    }

    private suspend fun validateAndSubmit(username: String, password: String) {
        val usernameOk = usernameValidationUseCase(username).success
        val passwordOk = passwordValidationUseCase(password).success
        if (!usernameOk || !passwordOk) {
            mutableStateFlow.value = LoginState(
                usernameError = if (!usernameOk) Res.string.feature_auth_error_username_length else null,
                passwordError = if (!passwordOk) Res.string.feature_auth_error_password_length else null,
            )
            return
        }
        // Clear any prior validation errors before submitting.
        mutableStateFlow.value = LoginState()
        pendingCredentials = username to password
        submit.submit { loginRepository.login(username, password) }
    }

    private suspend fun persistUser(
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
    }
}
