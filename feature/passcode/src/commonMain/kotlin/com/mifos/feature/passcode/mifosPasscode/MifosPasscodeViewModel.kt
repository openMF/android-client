/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.mifosPasscode

import androidclient.feature.passcode.generated.resources.Res
import androidclient.feature.passcode.generated.resources.feature_authenticator_setup_biometrics_prompt
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.AppLockRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeResult
import org.mifos.authenticator.passcode.PasscodeStep
import template.core.base.ui.BaseViewModel

/**
 * ViewModel for [MifosPasscode].
 *
 * Owns the glue between:
 *  - [PasscodeManager] (passcode library) — provides step/state + emits
 *    [PasscodeResult]s.
 *  - [PlatformAuthenticationProvider] (biometrics library) — provides
 *    `isRegistered`/status + biometric prompt invocation.
 *  - [AppLockRepository] — tracks the app-wide lock state.
 *
 * The provider is **not** injected here; it's composition-scoped and threaded
 * in via [MifosPasscodeAction.OnResume], [MifosPasscodeAction.OnAuthenticatorClick],
 * and [MifosPasscodeAction.ForgetPasscode].
 *
 * Lifecycle:
 *  - `OnStart` — if the screen is in `Enter` step, re-lock the app.
 *  - `OnResume` — if biometrics are allowed, available, and registered, and the
 *    screen is in `Enter` step, auto-trigger the biometric prompt.
 *  - `HandlePasscodeResult(Verified)` — unlock the app.
 *  - `ForgetPasscode` — clear app lock + biometric registration (separate
 *    action so the provider is only carried on the path that needs it).
 */
class MifosPasscodeViewModel(
    private val passcodeManager: PasscodeManager,
    private val appLockRepository: AppLockRepository,
) : BaseViewModel<MifosPasscodeState, MifosPasscodeEvent, MifosPasscodeAction>(
    initialState = MifosPasscodeState(),
) {

    override fun handleAction(action: MifosPasscodeAction) {
        when (action) {
            is MifosPasscodeAction.OnStart -> {
                if (passcodeManager.state.value.passcodeStep == PasscodeStep.Enter) {
                    appLockRepository.lockApp()
                }
            }

            is MifosPasscodeAction.OnResume -> {
                handleResume(action.systemAuthProvider, action.allowBiometricAuth)
            }

            is MifosPasscodeAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MifosPasscodeAction.HandlePasscodeResult -> {
                when (action.result) {
                    PasscodeResult.Verified -> appLockRepository.unlockApp()
                    PasscodeResult.Created,
                    PasscodeResult.Changed,
                    PasscodeResult.Rejected,
                    PasscodeResult.Forgotten,
                    -> { }
                }
                sendEvent(MifosPasscodeEvent.NavigateForResult(action.result))
            }

            is MifosPasscodeAction.ForgetPasscode -> {
                appLockRepository.deleteLock()
                viewModelScope.launch {
                    action.systemAuthProvider.unregister()
                }
                sendEvent(MifosPasscodeEvent.NavigateForResult(PasscodeResult.Forgotten))
            }

            is MifosPasscodeAction.OnAuthenticatorClick -> {
                authenticateWithBiometrics(action.systemAuthProvider)
            }

            MifosPasscodeAction.ClickConfirmOnNotRegisteredDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }

    /**
     * Auto-triggers biometric authentication on resume — but only when **all**
     * of the following are true:
     *  - [allowBiometricAuth] is `true` (caller's UI-layer guard).
     *  - The authenticator status contains [PlatformAuthenticatorStatus.BIOMETRICS_SET].
     *  - The passcode screen is in [PasscodeStep.Enter] (not Create/Confirm/ChangeVerify).
     *  - The provider's `isRegistered` flow is `true`.
     *
     * All four gates must agree; relaxing any one would silently re-enable
     * biometric auth in flows where it should be suppressed (e.g., change-passcode).
     */
    private fun handleResume(
        systemAuthProvider: PlatformAuthenticationProvider,
        allowBiometricAuth: Boolean,
    ) {
        if (!allowBiometricAuth) return
        val biometricsStatus = systemAuthProvider.authenticatorStatus.value
        if (
            biometricsStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET) &&
            passcodeManager.state.value.passcodeStep == PasscodeStep.Enter &&
            systemAuthProvider.isRegistered.value
        ) {
            authenticateWithBiometrics(systemAuthProvider)
        }
    }

    /**
     * Runs the biometric prompt and converts its outcome into UI state +
     * navigation events.
     *
     * Branch behaviour:
     *  - [AuthenticationResult.Success] — **re-checks** `passcodeStep == Enter`
     *    before unlocking. This guards against the step transitioning between
     *    when `onAuthenticatorClick` was dispatched and when it returns (e.g.,
     *    [handleResume] kicks off auto-auth, then the user navigates into
     *    change-passcode while the prompt is on screen, so by the time the
     *    prompt succeeds the step is `ChangeVerify`). If still in `Enter`,
     *    unlocks the app and emits [MifosPasscodeEvent.NavigateForResult]
     *    with [PasscodeResult.Verified] so biometric success converges on the
     *    same composable-level callback as passcode success. If the step has
     *    changed, the result is silently dropped — biometric auth is not
     *    allowed to satisfy a change-passcode / disable-biometrics flow.
     *  - [AuthenticationResult.UserNotRegistered] — the library has already
     *    cleared the stored blob and flipped `isRegistered` to `false`;
     *    surfaces the "re-setup" prompt dialog so the user is told to
     *    re-enroll.
     *  - [AuthenticationResult.Error] — renders a generic error dialog with
     *    the platform-provided message.
     *  - [AuthenticationResult.UserCancelled] — silent; no UI effect.
     */
    private fun authenticateWithBiometrics(
        systemAuthProvider: PlatformAuthenticationProvider,
    ) {
        viewModelScope.launch {
            val result = systemAuthProvider.onAuthenticatorClick(appName = "Mifos Pay")
            when (result) {
                is AuthenticationResult.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = PasscodeDialogState.Error(result.message),
                        )
                    }
                }

                AuthenticationResult.Success -> {
                    if (passcodeManager.state.value.passcodeStep == PasscodeStep.Enter) {
                        appLockRepository.unlockApp()
                        sendEvent(MifosPasscodeEvent.NavigateForResult(PasscodeResult.Verified))
                    }
                }

                AuthenticationResult.UserNotRegistered -> {
                    val message = getString(Res.string.feature_authenticator_setup_biometrics_prompt)
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = PasscodeDialogState.UserNotRegistered(
                                message = message,
                            ),
                        )
                    }
                }

                AuthenticationResult.UserCancelled -> {}
            }
        }
    }
}

/**
 * UI state for [MifosPasscodeViewModel].
 *
 * @property dialogState Non-null when an error / not-registered dialog should
 *           be shown to the user. Cleared via
 *           [MifosPasscodeAction.DismissDialog] or
 *           [MifosPasscodeAction.ClickConfirmOnNotRegisteredDialog].
 */
data class MifosPasscodeState(
    val dialogState: PasscodeDialogState? = null,
)

/** Dialog variants surfaced by [MifosPasscodeViewModel]. */
sealed interface PasscodeDialogState {
    /** Generic biometric error with a platform-provided message. */
    data class Error(val message: String) : PasscodeDialogState

    /**
     * The platform reported [AuthenticationResult.UserNotRegistered]. The
     * biometrics library has already wiped the invalid stored blob; confirming
     * this dialog is a terminal state (user must re-enroll in settings).
     */
    data class UserNotRegistered(
        val message: String,
    ) : PasscodeDialogState
}

/** Actions dispatched to [MifosPasscodeViewModel]. */
sealed interface MifosPasscodeAction {
    /** Fired on `Lifecycle.Event.ON_START`. Re-locks the app when in `Enter` step. */
    data object OnStart : MifosPasscodeAction

    /**
     * Fired on `Lifecycle.Event.ON_RESUME`. Carries the provider so the VM can
     * drive auto-auth (see [handleResume]).
     */
    data class OnResume(
        val systemAuthProvider: PlatformAuthenticationProvider,
        val allowBiometricAuth: Boolean,
    ) : MifosPasscodeAction

    /** User dismissed an error/not-registered dialog via system back or outside-tap. */
    data object DismissDialog : MifosPasscodeAction

    /**
     * Non-`Forgotten` [PasscodeResult] from the library. On `Verified` the VM
     * unlocks the app; other cases just forward the event to the screen for
     * navigation.
     */
    data class HandlePasscodeResult(val result: PasscodeResult) : MifosPasscodeAction

    /**
     * Dispatched for the `Forgotten` case only. Split from [HandlePasscodeResult]
     * so the provider reference (needed for `unregister()`) is only carried on
     * the one action that uses it.
     */
    data class ForgetPasscode(
        val systemAuthProvider: PlatformAuthenticationProvider,
    ) : MifosPasscodeAction

    /**
     * User tapped the biometric button. Carries the provider so the VM can
     * invoke `onAuthenticatorClick()`.
     */
    data class OnAuthenticatorClick(
        val systemAuthProvider: PlatformAuthenticationProvider,
    ) : MifosPasscodeAction

    /** User tapped OK on the [PasscodeDialogState.UserNotRegistered] dialog. */
    data object ClickConfirmOnNotRegisteredDialog : MifosPasscodeAction
}

/** One-shot events emitted by [MifosPasscodeViewModel] to the screen. */
sealed interface MifosPasscodeEvent {
    /**
     * Navigate based on the resulting [PasscodeResult]. Biometric success is
     * re-emitted as [PasscodeResult.Verified] here so the composable's single
     * result-switch handles both paths.
     */
    data class NavigateForResult(val result: PasscodeResult) : MifosPasscodeEvent
}
