/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.mifosPasscode

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.mifos.core.designsystem.component.MifosDialogBox
import androidclient.feature.passcode.generated.resources.Res
import androidclient.feature.passcode.generated.resources.feature_authenticator_error
import androidclient.feature.passcode.generated.resources.feature_authenticator_ok
import com.mifos.feature.passcode.components.BiometricsKey
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.authenticator.biometrics.platformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAvailableAuthenticationOption
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeResult
import org.mifos.authenticator.passcode.screen.PasscodeAppearanceConfig
import org.mifos.authenticator.passcode.screen.PasscodeButtonConfig
import org.mifos.authenticator.passcode.screen.PasscodeDialogConfig
import org.mifos.authenticator.passcode.screen.PasscodeDotConfig
import org.mifos.authenticator.passcode.screen.PasscodeKeyConfig
import org.mifos.authenticator.passcode.screen.PasscodeLogoConfig
import org.mifos.authenticator.passcode.screen.PasscodeScreen
import org.mifos.authenticator.passcode.screen.PasscodeSwitchConfig
import template.core.base.designsystem.theme.KptTheme

internal object MifosPasscodeCurrentInfo : NavigationEventInfo()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MifosPasscode(
    onAuthenticationSuccess: () -> Unit,
    onBackPress: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    onPasscodeCreation: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onAuthenticationFailed: () -> Unit = {},
    onBiometricsDisabled: () -> Unit = {},
    allowBackNavigation: Boolean = false,
    viewModel: MifosPasscodeViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val passcodeManager: PasscodeManager = koinInject<PasscodeManager>()

    val systemAuthProvider = platformAuthenticationProvider.current
    val systemAvailableAuthOption = platformAvailableAuthenticationOption.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    val navEventState = rememberNavigationEventState(
        currentInfo = MifosPasscodeCurrentInfo,
    )

    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = true,
        onBackCancelled = { },
        onBackCompleted = {
            if(allowBackNavigation){
                onBackPress()
            }
        },
    )

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is MifosPasscodeEvent.NavigateForResult -> {
                    when (event.result) {
                        PasscodeResult.Verified -> onAuthenticationSuccess()
                        PasscodeResult.Created -> onPasscodeCreation()
                        PasscodeResult.Changed -> onPasscodeChanged()
                        PasscodeResult.Forgotten -> navigateToLogin()
                        PasscodeResult.ExternalAuthDisabled -> onBiometricsDisabled()
                        PasscodeResult.Rejected -> onAuthenticationFailed()
                    }
                }
            }
        }
    }

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.trySendAction(MifosPasscodeAction.OnStart)
                }

                Lifecycle.Event.ON_RESUME -> {
                    viewModel.trySendAction(
                        MifosPasscodeAction.OnResume(
                            systemAuthProvider = systemAuthProvider,
                        ),
                    )
                }
                else -> {}
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose { lifeCycleOwner.lifecycle.removeObserver(observer) }
    }

    MifosDialogBox(
        title = stringResource(Res.string.feature_authenticator_error),
        showDialogState = state.dialogState != null,
        confirmButtonText = stringResource(Res.string.feature_authenticator_ok),
        dismissButtonText = "",
        onConfirm = {
            when (state.dialogState) {
                is PasscodeDialogState.UserNotRegistered -> {
                    viewModel.trySendAction(MifosPasscodeAction.ClickConfirmOnNotRegisteredDialog)
                }
                else -> viewModel.trySendAction(MifosPasscodeAction.DismissDialog)
            }
        },
        onDismiss = {
            viewModel.trySendAction(MifosPasscodeAction.DismissDialog)
        },
        message = when (val dialogState = state.dialogState) {
            is PasscodeDialogState.Error -> dialogState.message
            is PasscodeDialogState.UserNotRegistered -> dialogState.message
            null -> null
        },
    )

    PasscodeScreen(
        passcodeManager = passcodeManager,
        onResult = { result ->
            viewModel.trySendAction(MifosPasscodeAction.HandlePasscodeResult(result))
        },
        appearanceConfig = PasscodeAppearanceConfig(
            backgroundColor = KptTheme.colorScheme.background,
            headerTextStyle = KptTheme.typography.headlineMedium,
        ),
        logoConfig = PasscodeLogoConfig(),
        dotConfig = PasscodeDotConfig(
            dotColor = KptTheme.colorScheme.primary,
            inactiveDotColor = KptTheme.colorScheme.onBackground,
            visiblePasscodeTextStyle = KptTheme.typography.headlineSmall,
        ),
        keyConfig = PasscodeKeyConfig(
            shouldShuffleKeys = true,
            keyTextStyle = null,
            keyColor = KptTheme.colorScheme.primary,
            keyShape = CircleShape,
            keyElevation = null,
            keyContainerColor = KptTheme.colorScheme.surface,
            keySize = 60.dp,
        ),
        buttonConfig = PasscodeButtonConfig(
            skipButtonTextStyle = KptTheme.typography.labelLarge,
            forgotButtonTextStyle = KptTheme.typography.labelLarge,
        ),
        switchConfig = PasscodeSwitchConfig(
            switchTabColor = KptTheme.colorScheme.primary,
            switchEnabledColor = KptTheme.colorScheme.surfaceContainerHighest,
            switchEnabledTextColor = KptTheme.colorScheme.onSurface,
            switchDisabledTextColor = KptTheme.colorScheme.surface,
            switchTextStyle = null,
        ),
        dialogConfig = PasscodeDialogConfig(
            dialogContainerColor = KptTheme.colorScheme.surface,
            dialogTitleColor = KptTheme.colorScheme.onSurface,
            dialogButtonTextColor = KptTheme.colorScheme.onSurface,
            dialogShape = null,
        ),
        externalAuthButton = { modifier ->
            BiometricsKey(
                modifier = modifier,
                systemAvailableAuthOption = systemAvailableAuthOption,
                onClick = {
                    viewModel.trySendAction(
                        MifosPasscodeAction.OnAuthenticatorClick(
                            systemAuthProvider = systemAuthProvider,
                        ),
                    )
                },
            )
        },
    )
}
