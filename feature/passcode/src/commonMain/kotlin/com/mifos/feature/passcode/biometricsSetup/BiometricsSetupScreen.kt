/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.biometricsSetup

import androidclient.feature.passcode.generated.resources.Res
import androidclient.feature.passcode.generated.resources.feature_authenticator_biometrics_usage_message
import androidclient.feature.passcode.generated.resources.feature_authenticator_error
import androidclient.feature.passcode.generated.resources.feature_authenticator_fingerprint_icon
import androidclient.feature.passcode.generated.resources.feature_authenticator_ok
import androidclient.feature.passcode.generated.resources.feature_authenticator_secure_your_app
import androidclient.feature.passcode.generated.resources.feature_authenticator_setup_biometrics
import androidclient.feature.passcode.generated.resources.feature_authenticator_skip_for_now
import androidclient.feature.passcode.generated.resources.fingerprint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.mifos.core.designsystem.component.MifosDialogBox
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.MifosTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import template.core.base.designsystem.theme.KptTheme

/** Navigation-event info marker for the biometric-setup destination. */
internal object BiometricSetupScreenCurrentInfo : NavigationEventInfo()

/**
 * First-time biometric setup screen.
 *
 * Shown immediately after the user creates their passcode. Offers two paths:
 *  - "Setup biometrics" — calls `PlatformAuthenticationProvider.registerUser()`
 *    (via [BiometricSetupScreenViewmodel]); on success fires [onBiometricsRegistrationSuccess].
 *  - "Skip for now" — fires [onSkipBiometricSetup] without registering.
 *
 * Back-press is swallowed on this screen; the user must complete either path.
 * Must be hosted inside a `PlatformAuthenticatorCompositionProvider` so the
 * `platformAuthenticationProvider` CompositionLocal resolves.
 *
 * @param onBiometricsRegistrationSuccess Fired on `RegistrationResult.Success`.
 * @param onSkipBiometricSetup Fired when user taps the skip button.
 * @param viewModel Koin-resolved [BiometricSetupScreenViewmodel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricSetupScreen(
    onBiometricsRegistrationSuccess: () -> Unit,
    onSkipBiometricSetup: () -> Unit,
    viewModel: BiometricSetupScreenViewmodel = koinViewModel(),
) {
    val navEventState = rememberNavigationEventState(
        currentInfo = BiometricSetupScreenCurrentInfo,
    )

    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = true,
        onBackCancelled = { },
        onBackCompleted = { },
    )

    val platformAuthenticationProvider = platformAuthenticationProvider.current

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                BiometricSetupScreenEvent.OnBiometricSetupSuccess -> onBiometricsRegistrationSuccess()
                BiometricSetupScreenEvent.OnSkipBiometricSetup -> onSkipBiometricSetup()
            }
        }
    }

    BiometricSetupContent(
        state = state,
        onSetupBiometrics = {
            viewModel.trySendAction(
                BiometricSetupScreenAction.ClickSetupBiometric(platformAuthenticationProvider),
            )
        },
        onSkipBiometricSetup = {
            viewModel.trySendAction(BiometricSetupScreenAction.ClickSkipBiometric)
        },
        onDismissErrorDialog = {
            viewModel.trySendAction(BiometricSetupScreenAction.DismissErrorDialog)
        },
    )
}

/**
 * Stateless presentation layer for [BiometricSetupScreen]. Extracted so the
 * `@Preview` below can exercise the UI without touching Koin or the provider.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BiometricSetupContent(
    state: BiometricSetupScreenState,
    onSetupBiometrics: () -> Unit,
    onSkipBiometricSetup: () -> Unit,
    onDismissErrorDialog: () -> Unit,
) {
    MifosDialogBox(
        title = stringResource(Res.string.feature_authenticator_error),
        showDialogState = state.error != null,
        confirmButtonText = stringResource(Res.string.feature_authenticator_ok),
        dismissButtonText = "",
        onConfirm = onDismissErrorDialog,
        onDismiss = onDismissErrorDialog,
        message = state.error,
    )

    MifosScaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KptTheme.colorScheme.background)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.fingerprint),
                contentDescription = stringResource(Res.string.feature_authenticator_fingerprint_icon),
                modifier = Modifier.size(120.dp),
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text = stringResource(Res.string.feature_authenticator_secure_your_app),
                style = KptTheme.typography.headlineSmall,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.feature_authenticator_biometrics_usage_message),
                style = KptTheme.typography.bodyLarge,
                color = KptTheme.colorScheme.inverseSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onSetupBiometrics,
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(20),
            ) {
                Text(
                    text = stringResource(Res.string.feature_authenticator_setup_biometrics),
                    color = KptTheme.colorScheme.onPrimary,
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = onSkipBiometricSetup,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(Res.string.feature_authenticator_skip_for_now))
            }
        }
    }
}

@Preview
@Composable
private fun BiometricSetupScreenPreview() {
    MifosTheme {
        BiometricSetupContent(
            state = BiometricSetupScreenState(),
            onSetupBiometrics = {},
            onSkipBiometricSetup = {},
            onDismissErrorDialog = {},
        )
    }
}
