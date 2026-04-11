/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.biometricsSetup

import androidclient.feature.passcode.generated.resources.Res
import androidclient.feature.passcode.generated.resources.feature_authenticator_biometrics_available
import androidclient.feature.passcode.generated.resources.feature_authenticator_biometrics_not_set
import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import org.mifos.authenticator.passcode.PasscodeManager
import template.core.base.ui.BaseViewModel

private const val DEFAULT_USER_ID = "default_user"
private const val DEFAULT_USER_EMAIL = "default@mifos.org"
private const val DEFAULT_DISPLAY_NAME = "Mifos User"

class BiometricSetupScreenViewmodel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val passcodeManager: PasscodeManager,
    private val biometricStorageAdapter: BiometricStorageAdapter,
) : BaseViewModel<
    BiometricSetupScreenState,
    BiometricSetupScreenEvent,
    BiometricSetupScreenAction,
    >(BiometricSetupScreenState()) {

    override fun handleAction(action: BiometricSetupScreenAction) {
        when (action) {
            is BiometricSetupScreenAction.ClickSetupBiometric -> {
                registerUser(action.platformAuthenticationProvider)
            }
            BiometricSetupScreenAction.ClickSkipBiometric -> {
                sendEvent(BiometricSetupScreenEvent.OnSkipBiometricSetup)
            }
            BiometricSetupScreenAction.DismissErrorDialog -> {
                mutableStateFlow.update {
                    it.copy(error = null)
                }
            }
        }
    }

    private fun registerUser(platformAuthenticationProvider: PlatformAuthenticationProvider) {
        viewModelScope.launch {
            val user = userPreferencesRepository.userData.first()
            val result = platformAuthenticationProvider.registerUser(
                user.userId.takeIf { it != 0L }?.toString() ?: DEFAULT_USER_ID,
                user.username ?: DEFAULT_USER_EMAIL,
                user.username ?: DEFAULT_DISPLAY_NAME,
            )

            when (result) {
                is RegistrationResult.Success -> {
                    biometricStorageAdapter.saveRegistrationData(result.message)
                    passcodeManager.setExternalAuthEnabled(true)
                    sendEvent(BiometricSetupScreenEvent.OnBiometricSetupSuccess)
                }
                RegistrationResult.PlatformAuthenticatorNotSet -> {
                    mutableStateFlow.update {
                        it.copy(
                            error = getString(Res.string.feature_authenticator_biometrics_not_set),
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                    mutableStateFlow.update {
                        it.copy(
                            error = getString(Res.string.feature_authenticator_biometrics_available),
                        )
                    }
                }
                is RegistrationResult.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            error = result.message,
                        )
                    }
                }
                RegistrationResult.UserCancelled -> { }
            }
        }
    }
}

data class BiometricSetupScreenState(
    val error: String? = null,
)

sealed interface BiometricSetupScreenAction {
    data object DismissErrorDialog : BiometricSetupScreenAction
    data object ClickSkipBiometric : BiometricSetupScreenAction

    data class ClickSetupBiometric(
        val platformAuthenticationProvider: PlatformAuthenticationProvider,
    ) : BiometricSetupScreenAction
}

sealed interface BiometricSetupScreenEvent {
    data object OnSkipBiometricSetup : BiometricSetupScreenEvent
    data object OnBiometricSetupSuccess : BiometricSetupScreenEvent
}
