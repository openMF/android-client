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
import androidclient.feature.auth.generated.resources.feature_auth_enter_credentials
import androidclient.feature.auth.generated.resources.feature_auth_mifos_logo
import androidclient.feature.auth.generated.resources.feature_auth_password
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.component.MifosAndroidClientIcon
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Aditya Gupta on 11/02/24.
 */

@Composable
internal fun LoginScreen(
    homeIntent: () -> Unit,
    passcodeIntent: () -> Unit,
    onClickToUpdateServerConfig: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
) {
    val state = loginViewModel.loginUiState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val showDialog = rememberSaveable { mutableStateOf(false) }

    var userName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }

    val usernameError: MutableState<StringResource?> = remember { mutableStateOf(null) }
    val passwordError: MutableState<StringResource?> = remember { mutableStateOf(null) }

    when (state) {
        is LoginUiState.Empty -> {}

        is LoginUiState.ShowError -> {
            showDialog.value = false
            LaunchedEffect(key1 = state.message) {
                snackbarHostState.showSnackbar(message = getString(state.message))
            }
        }

        is LoginUiState.ShowProgress -> {
            showDialog.value = true
        }

        is LoginUiState.ShowValidationError -> {
            usernameError.value = state.usernameError
            passwordError.value = state.passwordError
        }

        LoginUiState.HomeActivityIntent -> {
            showDialog.value = false
            homeIntent()
        }

        LoginUiState.PassCodeActivityIntent -> {
            showDialog.value = false
            passcodeIntent()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        containerColor = White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                FilledTonalButton(
                    onClick = onClickToUpdateServerConfig,
                    modifier = Modifier
                        .align(Alignment.Center),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                    ),
                ) {
                    Text(text = "Update Server Configuration")

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = MifosIcons.ArrowForward,
                        contentDescription = "ArrowForward",
                    )
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Spacer(modifier = Modifier.height(80.dp))

            MifosAndroidClientIcon(imageVector = painterResource(Res.drawable.feature_auth_mifos_logo))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = stringResource(Res.string.feature_auth_enter_credentials),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            MifosOutlinedTextField(
                value = userName,
                onValueChanged = { value ->
                    userName = value
                },
                icon = MifosIcons.Person,
                label = stringResource(Res.string.feature_auth_username),
                error = usernameError.value?.let { it1 -> stringResource(it1) },
                trailingIcon = {
                    if (usernameError.value != null) {
                        Icon(imageVector = MifosIcons.Error, contentDescription = "Error Icon")
                    }
                },
            )

            Spacer(modifier = Modifier.height(6.dp))

            MifosOutlinedTextField(
                value = password,
                onValueChanged = { value ->
                    password = value
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                icon = MifosIcons.Lock,
                label = stringResource(Res.string.feature_auth_password),
                error = passwordError.value?.let { it1 -> stringResource(it1) },
                trailingIcon = {
                    if (passwordError.value == null) {
                        val image = if (passwordVisibility) {
                            MifosIcons.Visibility
                        } else {
                            MifosIcons.VisibilityOff
                        }
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(imageVector = image, "PasswordVisibility Icon")
                        }
                    } else {
                        Icon(MifosIcons.Error, contentDescription = null)
                    }
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        loginViewModel.validateUserInputs(userName.text, password.text)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp)
                    .padding(start = 16.dp, end = 16.dp),
                contentPadding = PaddingValues(),
            ) {
                Text(text = "Login", fontSize = 16.sp)
            }
        }
        if (showDialog.value) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@DevicePreview()
@Composable
private fun LoginScreenPreview() {
    LoginScreen({}, {}, {})
}
