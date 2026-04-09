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

import androidclient.feature.passcode.generated.resources.Res
import androidclient.feature.passcode.generated.resources.feature_authenticator_setup_biometrics_prompt
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.AppLockRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.biometrics.platformAuthenticator.AuthenticationResult
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticatorStatus
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeResult
import org.mifos.authenticator.passcode.PasscodeStep
import template.core.base.ui.BaseViewModel

class MifosPasscodeViewModel(
    private val passcodeManager: PasscodeManager,
    private val appLockRepository: AppLockRepository,
    private val biometricStorageAdapter: BiometricStorageAdapter,
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
                handleResume(action.systemAuthProvider)
            }

            is MifosPasscodeAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is MifosPasscodeAction.HandlePasscodeResult -> {
                when (action.result) {
                    PasscodeResult.Verified -> appLockRepository.unlockApp()
                    PasscodeResult.Forgotten -> {
                        appLockRepository.deleteLock()
                        biometricStorageAdapter.deleteRegistrationData()
                    }
                    PasscodeResult.Created,
                    PasscodeResult.Changed,
                    PasscodeResult.ExternalAuthDisabled,
                    PasscodeResult.Rejected,
                    -> { }
                }
                sendEvent(MifosPasscodeEvent.NavigateForResult(action.result))
            }

            is MifosPasscodeAction.OnAuthenticatorClick -> {
                authenticateWithBiometrics(action.systemAuthProvider)
            }

            MifosPasscodeAction.ClickConfirmOnNotRegisteredDialog -> {
                passcodeManager.setExternalAuthEnabled(false)
                biometricStorageAdapter.deleteRegistrationData()
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }

    private fun handleResume(
        systemAuthProvider: PlatformAuthenticationProvider,
    ) {
        val biometricsStatus = systemAuthProvider.authenticatorStatus.value
        if (
            biometricsStatus.contains(PlatformAuthenticatorStatus.BIOMETRICS_SET) &&
            passcodeManager.state.value.passcodeStep == PasscodeStep.Enter
        ) {
            authenticateWithBiometrics(systemAuthProvider)
        }
    }

    private fun authenticateWithBiometrics(
        systemAuthProvider: PlatformAuthenticationProvider,
    ) {
        viewModelScope.launch {
            val registrationData = biometricStorageAdapter.loadRegistrationData() ?: return@launch
            val result = systemAuthProvider.onAuthenticatorClick(
                appName = "Mifos Pay",
                savedRegistrationData = registrationData,
            )
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
                        passcodeManager.notifyExternalAuthSuccess()
                        appLockRepository.unlockApp()
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

data class MifosPasscodeState(
    val dialogState: PasscodeDialogState? = null,
)

sealed interface PasscodeDialogState {
    data class Error(val message: String) : PasscodeDialogState
    data class UserNotRegistered(
        val message: String,
    ) : PasscodeDialogState
}

sealed interface MifosPasscodeAction {
    data object OnStart : MifosPasscodeAction
    data class OnResume(
        val systemAuthProvider: PlatformAuthenticationProvider,
    ) : MifosPasscodeAction

    data object DismissDialog : MifosPasscodeAction
    data class HandlePasscodeResult(val result: PasscodeResult) : MifosPasscodeAction
    data class OnAuthenticatorClick(
        val systemAuthProvider: PlatformAuthenticationProvider,
    ) : MifosPasscodeAction
    data object ClickConfirmOnNotRegisteredDialog : MifosPasscodeAction
}

sealed interface MifosPasscodeEvent {
    data class NavigateForResult(val result: PasscodeResult) : MifosPasscodeEvent
}
