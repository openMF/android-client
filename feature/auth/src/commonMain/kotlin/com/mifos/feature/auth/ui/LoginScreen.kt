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
import androidclient.feature.auth.generated.resources.feature_auth_cd_arrow_forward
import androidclient.feature.auth.generated.resources.feature_auth_cd_error_icon
import androidclient.feature.auth.generated.resources.feature_auth_cd_password_visibility
import androidclient.feature.auth.generated.resources.feature_auth_enter_credentials
import androidclient.feature.auth.generated.resources.feature_auth_login
import androidclient.feature.auth.generated.resources.feature_auth_mifos_logo
import androidclient.feature.auth.generated.resources.feature_auth_password
import androidclient.feature.auth.generated.resources.feature_auth_update_server_configuration
import androidclient.feature.auth.generated.resources.feature_auth_username
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import template.core.base.store.submit.SubmitState
import com.mifos.core.designsystem.component.MifosAndroidClientIcon
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.network.PostAuthenticationResponse
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import template.core.base.ui.submit.MutationScreenContent

@Composable
internal fun LoginScreen(
    passcodeIntent: () -> Unit,
    onClickToUpdateServerConfig: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
) {
    val state by loginViewModel.stateFlow.collectAsState()
    val submitState by loginViewModel.submitState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        loginViewModel.eventFlow.collect { event ->
            when (event) {
                is LoginEvent.NavigateToPasscode -> passcodeIntent()
                is LoginEvent.ShowError -> {
                    snackbarHostState.showSnackbar(message = getString(event.message))
                    loginViewModel.actionChannel.trySend(LoginAction.DismissError)
                }
            }
        }
    }

    LoginContent(
        state = state,
        submitState = submitState,
        onSubmit = { username, password ->
            loginViewModel.actionChannel.trySend(LoginAction.Submit(username, password))
        },
        onClickToUpdateServerConfig = onClickToUpdateServerConfig,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun LoginContent(
    state: LoginState,
    submitState: SubmitState<PostAuthenticationResponse>,
    onSubmit: (username: String, password: String) -> Unit,
    onClickToUpdateServerConfig: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    // Login has no server-side data to fetch (the user isn't authenticated yet),
    // so screenState is a degenerate Content(Unit) — MutationScreenContent still
    // gives us the SubmitProgressOverlay + SubmitResultHandler for the submit
    // lifecycle, which is what we want for the consistent mutation-screen UX.
    val screenState = remember { ScreenState.Content(Unit, DataFreshness.FRESH) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KptTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignToken.padding.medium),
                contentAlignment = Alignment.Center,
            ) {
                FilledTonalButton(
                    onClick = onClickToUpdateServerConfig,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = KptTheme.colorScheme.tertiaryContainer,
                        contentColor = KptTheme.colorScheme.tertiary,
                    ),
                ) {
                    Text(text = stringResource(Res.string.feature_auth_update_server_configuration))
                    Spacer(modifier = Modifier.width(KptTheme.spacing.xs))
                    Icon(
                        imageVector = MifosIcons.ArrowForward,
                        contentDescription = stringResource(Res.string.feature_auth_cd_arrow_forward),
                    )
                }
            }
        },
    ) { paddingValues ->
        MutationScreenContent<Unit, PostAuthenticationResponse>(
            screenState = screenState,
            submitState = submitState,
            onRetry = {},
            // Terminal `Submitted` navigation is handled by LoginViewModel emitting
            // a NavigateToPasscode Event (after persistUser succeeds). The Screen
            // listens via LaunchedEffect on eventFlow — see LoginScreen above. So
            // MutationScreenContent's own onSubmitted is a no-op here.
            onSubmitted = { _ -> },
            modifier = Modifier.padding(paddingValues),
        ) { _, _ ->
            LoginForm(
                state = state,
                onSubmit = onSubmit,
                isSubmitting = submitState is SubmitState.Submitting,
            )
        }
    }
}

@Composable
private fun LoginForm(
    state: LoginState,
    onSubmit: (username: String, password: String) -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier,
) {
    var userName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.md)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(DesignToken.spacing.dp80))
        MifosAndroidClientIcon(imageVector = painterResource(Res.drawable.feature_auth_mifos_logo))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = KptTheme.spacing.sm),
            text = stringResource(Res.string.feature_auth_enter_credentials),
            textAlign = TextAlign.Center,
            style = KptTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = userName,
            onValueChanged = { userName = it },
            icon = MifosIcons.Person,
            label = stringResource(Res.string.feature_auth_username),
            error = state.usernameError?.let { stringResource(it) },
            trailingIcon = {
                if (state.usernameError != null) {
                    Icon(
                        imageVector = MifosIcons.Error,
                        contentDescription = stringResource(Res.string.feature_auth_cd_error_icon),
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.mediumSmall))

        MifosOutlinedTextField(
            value = password,
            onValueChanged = { password = it },
            visualTransformation = if (passwordVisibility) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            icon = MifosIcons.Lock,
            label = stringResource(Res.string.feature_auth_password),
            error = state.passwordError?.let { stringResource(it) },
            trailingIcon = {
                if (state.passwordError == null) {
                    val image = if (passwordVisibility) {
                        MifosIcons.Visibility
                    } else {
                        MifosIcons.VisibilityOff
                    }
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            imageVector = image,
                            contentDescription = stringResource(Res.string.feature_auth_cd_password_visibility),
                        )
                    }
                } else {
                    Icon(
                        imageVector = MifosIcons.Error,
                        contentDescription = stringResource(Res.string.feature_auth_cd_error_icon),
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

        Button(
            onClick = { onSubmit(userName.text, password.text) },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(DesignToken.spacing.dp44)
                .padding(horizontal = KptTheme.spacing.md),
            contentPadding = PaddingValues(),
        ) {
            Text(text = stringResource(Res.string.feature_auth_login), style = KptTheme.typography.bodyLarge)
        }
    }
}

// region Previews

@DevicePreview
@Composable
private fun LoginScreenIdlePreview() {
    LoginContent(
        state = LoginState(),
        submitState = SubmitState.Idle,
        onSubmit = { _, _ -> },
        onClickToUpdateServerConfig = {},
        snackbarHostState = remember { SnackbarHostState() },
    )
}

@DevicePreview
@Composable
private fun LoginScreenSubmittingPreview() {
    LoginContent(
        state = LoginState(),
        submitState = SubmitState.Submitting,
        onSubmit = { _, _ -> },
        onClickToUpdateServerConfig = {},
        snackbarHostState = remember { SnackbarHostState() },
    )
}

@DevicePreview
@Composable
private fun LoginScreenValidationErrorPreview() {
    LoginContent(
        state = LoginState(
            usernameError = Res.string.feature_auth_username,
            passwordError = Res.string.feature_auth_password,
        ),
        submitState = SubmitState.Idle,
        onSubmit = { _, _ -> },
        onClickToUpdateServerConfig = {},
        snackbarHostState = remember { SnackbarHostState() },
    )
}

// endregion
