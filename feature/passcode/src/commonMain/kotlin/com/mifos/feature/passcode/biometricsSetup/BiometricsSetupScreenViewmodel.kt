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
import androidclient.feature.passcode.generated.resources.feature_authenticator_biometrics_not_available
import androidclient.feature.passcode.generated.resources.feature_authenticator_biometrics_not_set
import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import template.core.base.ui.BaseViewModel

private const val DEFAULT_USER_ID = "default_user"
private const val DEFAULT_USER_EMAIL = "default@mifos.org"
private const val DEFAULT_DISPLAY_NAME = "Mifos User"

/**
 * ViewModel for the first-time biometric setup screen.
 *
 * Invoked after a successful passcode creation. Drives the register-user call
 * against the platform authenticator; on success, the biometrics library
 * persists the registration blob internally (no adapter call here). Dialog
 * state surfaces registration failures for user-visible handling.
 *
 * Identity seed: prefers the signed-in user from [UserPreferencesRepository];
 * falls back to [DEFAULT_USER_ID] / [DEFAULT_USER_EMAIL] / [DEFAULT_DISPLAY_NAME]
 * only when those fields are absent on the user record.
 */
class BiometricSetupScreenViewmodel(
    private val userPreferencesRepository: UserPreferencesRepository,
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
                            error = getString(Res.string.feature_authenticator_biometrics_not_available),
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

/**
 * UI state for [BiometricSetupScreenViewmodel].
 *
 * @property error Non-null when a registration error should be shown to the user
 *           via the screen's dialog. Cleared on
 *           [BiometricSetupScreenAction.DismissErrorDialog].
 */
data class BiometricSetupScreenState(
    val error: String? = null,
)

/** Actions dispatched to [BiometricSetupScreenViewmodel]. */
sealed interface BiometricSetupScreenAction {
    /** Dismisses any currently-shown error dialog. */
    data object DismissErrorDialog : BiometricSetupScreenAction

    /** User tapped "Skip for now." Emits [BiometricSetupScreenEvent.OnSkipBiometricSetup]. */
    data object ClickSkipBiometric : BiometricSetupScreenAction

    /**
     * User tapped "Setup biometrics." Kicks off the platform register flow.
     *
     * @property platformAuthenticationProvider Provider obtained from the
     *           `platformAuthenticationProvider` CompositionLocal; passed in
     *           because VMs cannot inject the composition-scoped provider.
     */
    data class ClickSetupBiometric(
        val platformAuthenticationProvider: PlatformAuthenticationProvider,
    ) : BiometricSetupScreenAction
}

/** One-shot navigation events emitted by [BiometricSetupScreenViewmodel]. */
sealed interface BiometricSetupScreenEvent {
    /** User skipped setup. Route handler should proceed past this screen. */
    data object OnSkipBiometricSetup : BiometricSetupScreenEvent

    /** Registration succeeded; the library has already persisted the blob. */
    data object OnBiometricSetupSuccess : BiometricSetupScreenEvent
}
