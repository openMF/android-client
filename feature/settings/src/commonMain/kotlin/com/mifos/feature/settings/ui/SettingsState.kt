/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.ui

import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.model.objects.LanguageConfig
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider

/**
 * MVI state for the Settings list screen.
 *
 * Settings is a 100%-local screen: every value here comes from
 * `UserPreferencesRepository.settingsInfo` (datastore) — no HTTP. The
 * biometrics row is mirrored from the platform authenticator library's
 * `isRegistered` flow at the composable layer (single source of truth — see
 * RULE-STATE-SINGLE-SOURCE).
 */
data class SettingsState(
    val tenant: String = "",
    val baseUrl: String = "",
    val passcode: String = "",
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: LanguageConfig = LanguageConfig.DEFAULT,

    /** Non-null when a biometrics-operation error dialog should be shown. */
    val biometricsError: String? = null,

    // Dialog visibility — kept in state so they survive configuration changes.
    val showLanguageDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showEndpointDialog: Boolean = false,
    val showSyncSurveyDialog: Boolean = false,
)

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object NavigateToChangePasscode : SettingsEvent
    data object NavigateToUpdateServerConfig : SettingsEvent
    data object NavigateToLogin : SettingsEvent

    /**
     * The user toggled biometrics off. The screen routes to the internal
     * passcode screen with [verificationKey] so the library can mint a
     * short-lived token; the result is delivered back via SavedStateHandle and
     * consumed by [SettingsViewModel.onDisableBiometricsResult].
     */
    data class RequestDisableBiometrics(val verificationKey: String) : SettingsEvent

    /** Locale change — the host activity reapplies the locale. */
    data class UpdateLocale(val languageCode: String, val isSystemLanguage: Boolean) : SettingsEvent
}

sealed interface SettingsAction {
    data object NavigateBack : SettingsAction

    // Card taps.
    data object OnClickSyncSurvey : SettingsAction
    data object OnClickLanguage : SettingsAction
    data object OnClickTheme : SettingsAction
    data object OnClickChangePasscode : SettingsAction
    data object OnClickBiometrics : SettingsAction
    data object OnClickEndpoint : SettingsAction
    data object OnClickServerConfig : SettingsAction

    // Dialog dismissals.
    data object DismissSyncSurveyDialog : SettingsAction
    data object DismissLanguageDialog : SettingsAction
    data object DismissThemeDialog : SettingsAction
    data object DismissEndpointDialog : SettingsAction
    data object DismissBiometricsError : SettingsAction

    // Mutations driven by the dialogs.
    data class UpdateTheme(val theme: AppTheme) : SettingsAction
    data class UpdateLanguage(val language: com.mifos.core.common.enums.MifosAppLanguage) : SettingsAction
    data class UpdateEndpoint(val baseUrl: String, val tenant: String) : SettingsAction
    data class RegisterBiometrics(val provider: PlatformAuthenticationProvider) : SettingsAction
    data class DisableBiometricsResult(
        val success: Boolean,
        val provider: PlatformAuthenticationProvider,
    ) : SettingsAction
    data object RequestDisableBiometrics : SettingsAction
}
