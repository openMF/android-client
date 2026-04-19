/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.settings

import androidclient.feature.settings.generated.resources.Res
import androidclient.feature.settings.generated.resources.feature_settings_biometrics_not_available
import androidclient.feature.settings.generated.resources.feature_settings_biometrics_not_set
import androidclient.feature.settings.generated.resources.feature_settings_change_passcode
import androidclient.feature.settings.generated.resources.feature_settings_change_passcode_desc
import androidclient.feature.settings.generated.resources.feature_settings_enable_disable_biometrics
import androidclient.feature.settings.generated.resources.feature_settings_enable_disable_biometrics_desc
import androidclient.feature.settings.generated.resources.feature_settings_instance_url
import androidclient.feature.settings.generated.resources.feature_settings_instance_url_desc
import androidclient.feature.settings.generated.resources.feature_settings_language
import androidclient.feature.settings.generated.resources.feature_settings_language_desc
import androidclient.feature.settings.generated.resources.feature_settings_server_config
import androidclient.feature.settings.generated.resources.feature_settings_server_config_desc
import androidclient.feature.settings.generated.resources.feature_settings_sync_survey
import androidclient.feature.settings.generated.resources.feature_settings_sync_survey_desc
import androidclient.feature.settings.generated.resources.feature_settings_theme
import androidclient.feature.settings.generated.resources.feature_settings_theme_desc
import androidclient.feature.settings.generated.resources.feature_settings_verification_expired
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.data.repository.UserVerificationRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.datastore.model.DarkThemeConfig
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.LanguageConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import org.mifos.authenticator.passcode.PasscodeManager

/**
 * `SavedStateHandle` key used by [SettingsScreen] to receive the result of the
 * disable-biometrics passcode-verification round trip.
 *
 * The flow: Settings navigates to the internal passcode screen with this key;
 * the passcode screen writes `true` on verified, `false` on cancel/back/fail;
 * Settings observes the key and dispatches [SettingsViewModel.disableBiometrics].
 */
const val DISABLE_BIOMETRICS_VERIFICATION_KEY = "com.mifos.authentication.verification.key"
private const val DEFAULT_USER_ID = "default_user"
private const val DEFAULT_USER_EMAIL = "default@mifos.org"
private const val DEFAULT_DISPLAY_NAME = "Mifos User"

class SettingsViewModel(
    private val prefManager: UserPreferencesRepository,
    private val passcodeManager: PasscodeManager,
    private val userVerificationRepository: UserVerificationRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = prefManager
        .settingsInfo
        .map { settings ->
            SettingsUiState(
                tenant = settings.tenant,
                baseUrl = settings.baseUrl,
                passcode = settings.passcode ?: "",
                theme = when (settings.appTheme) {
                    DarkThemeConfig.LIGHT -> AppTheme.LIGHT
                    DarkThemeConfig.DARK -> AppTheme.DARK
                    else -> AppTheme.SYSTEM
                },
                language = settings.language,

            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.DEFAULT)

    private val _biometricsState = MutableStateFlow(BiometricsState())
    val biometricsState: StateFlow<BiometricsState> = _biometricsState.asStateFlow()

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            prefManager.updateTheme(
                when (theme) {
                    AppTheme.LIGHT -> DarkThemeConfig.LIGHT
                    AppTheme.DARK -> DarkThemeConfig.DARK
                    AppTheme.SYSTEM -> DarkThemeConfig.FOLLOW_SYSTEM
                },
            )
        }
    }

    /**
     * Consumes the disable-biometrics passcode-verification result.
     *
     * Called from [SettingsScreen]'s `LaunchedEffect` when
     * [DISABLE_BIOMETRICS_VERIFICATION_KEY] flips from `null` to a boolean.
     *
     * On `true` (verified): consumes the one-shot
     * [UserVerificationRepository] token and, iff the token was still valid,
     * calls `authProvider.unregister()`. If the token had expired (30 s
     * window), surfaces an error dialog via [updateBiometricsErrorState] so
     * the user can retry.
     *
     * On `false` (cancelled, backed-out, or wrong passcode): simply clears the
     * savedStateHandle key. No unregister, no error dialog — the user is back
     * on Settings and the biometrics toggle still shows its pre-flow state.
     *
     * @param authenticationSuccess Result from the passcode verification round trip.
     * @param authProvider Composition-scoped biometrics provider, supplied by
     *        the screen at dispatch time.
     */
    fun disableBiometrics(
        authenticationSuccess: Boolean,
        authProvider: PlatformAuthenticationProvider,
    ) {
        viewModelScope.launch {
            when (authenticationSuccess) {
                true -> {
                    if (userVerificationRepository.consumeVerification()) {
                        authProvider.unregister()
                    } else {
                        updateBiometricsErrorState(
                            getString(Res.string.feature_settings_verification_expired),
                        )
                    }
                    savedStateHandle.remove<Boolean?>(DISABLE_BIOMETRICS_VERIFICATION_KEY)
                }
                false -> {
                    savedStateHandle.remove<Boolean?>(DISABLE_BIOMETRICS_VERIFICATION_KEY)
                }
            }
        }
    }

    /**
     * Registers the user's biometrics with the platform authenticator. Called
     * directly from [SettingsScreen] when the "Enable Biometrics" button is
     * tapped; no passcode pre-verification is required (user already
     * authenticated to reach Settings).
     *
     * On [RegistrationResult.Success] the library persists the registration
     * blob internally; `authProvider.isRegistered` flips to `true` and the
     * Settings UI re-labels the button to "Disable Biometrics" reactively.
     *
     * On failure variants, pushes an error message into [BiometricsState.error]
     * for dialog display.
     *
     * @param systemAuthProvider Composition-scoped biometrics provider,
     *        supplied by the screen at dispatch time.
     */
    fun registerBiometrics(
        systemAuthProvider: PlatformAuthenticationProvider,
    ) {
        viewModelScope.launch {
            val result = systemAuthProvider.registerUser(
                userName = DEFAULT_USER_ID,
                emailId = DEFAULT_USER_EMAIL,
                displayName = DEFAULT_DISPLAY_NAME,
            )

            when (result) {
                is RegistrationResult.Success -> { }
                RegistrationResult.PlatformAuthenticatorNotSet -> {
                    updateBiometricsErrorState(
                        getString(Res.string.feature_settings_biometrics_not_set),
                    )
                }
                RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                    updateBiometricsErrorState(
                        getString(Res.string.feature_settings_biometrics_not_available),
                    )
                }
                is RegistrationResult.Error -> {
                    updateBiometricsErrorState(result.message)
                }

                RegistrationResult.UserCancelled -> {}
            }
        }
    }

    /**
     * Puts the library's [PasscodeManager] into `ChangeVerify` step. Caller is
     * expected to navigate to the internal passcode screen immediately after
     * so the user can verify + re-create.
     */
    fun changePasscode() {
        passcodeManager.changePasscode()
    }

    /**
     * Sets or clears the biometric-operation error surfaced via [BiometricsState.error].
     * Called both internally (on registration failure) and from the screen
     * (to dismiss after the user acknowledges the dialog).
     */
    fun updateBiometricsErrorState(error: String?) {
        _biometricsState.update {
            it.copy(error = error)
        }
    }

    fun updateLanguage(language: String): Boolean {
        return (language == MifosAppLanguage.SYSTEM_LANGUAGE.code)
    }

    fun tryUpdatingEndpoint(selectedBaseUrl: String, selectedTenant: String): Boolean {
        val isEndpointUpdated =
            !(uiState.value.baseUrl == selectedBaseUrl && uiState.value.tenant == selectedTenant)
        if (isEndpointUpdated) {
            viewModelScope.launch {
                prefManager.updateSettings(
                    prefManager.settingsInfo.first().copy(
                        baseUrl = selectedBaseUrl,
                        tenant = selectedTenant,
                    ),
                )
            }
        }
        return isEndpointUpdated
    }
}

enum class SettingsCardItem(
    val title: StringResource,
    val details: StringResource,
    val icon: ImageVector?,
) {
    SYNC_SURVEY(
        title = Res.string.feature_settings_sync_survey,
        details = Res.string.feature_settings_sync_survey_desc,
        icon = MifosIcons.Sync,
    ),
    LANGUAGE(
        title = Res.string.feature_settings_language,
        details = Res.string.feature_settings_language_desc,
        icon = MifosIcons.Language,
    ),
    THEME(
        title = Res.string.feature_settings_theme,
        details = Res.string.feature_settings_theme_desc,
        icon = MifosIcons.Theme,
    ),
    PASSCODE(
        title = Res.string.feature_settings_change_passcode,
        details = Res.string.feature_settings_change_passcode_desc,
        icon = MifosIcons.Password,
    ),
    BIOMETRICS(
        title = Res.string.feature_settings_enable_disable_biometrics,
        details = Res.string.feature_settings_enable_disable_biometrics_desc,
        icon = MifosIcons.Fingerprint,
    ),
    ENDPOINT(
        title = Res.string.feature_settings_instance_url,
        details = Res.string.feature_settings_instance_url_desc,
        icon = MifosIcons.AddLink,
    ),
    SERVER_CONFIG(
        title = Res.string.feature_settings_server_config,
        details = Res.string.feature_settings_server_config_desc,
        icon = MifosIcons.Update,
    ),
}

data class SettingsUiState(
    val tenant: String,
    val baseUrl: String,
    val passcode: String,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: LanguageConfig = LanguageConfig.DEFAULT,
) {
    companion object {
        val DEFAULT = SettingsUiState(
            tenant = "",
            baseUrl = "",
            passcode = "",
        )
    }
}

/**
 * Biometric-operation error state surfaced by [SettingsViewModel].
 *
 * Does **not** track registration status — that lives on
 * `authProvider.isRegistered` (library StateFlow), read directly at the
 * composable layer. Keeping a single source of truth avoids drift between
 * VM-local state and the library flow.
 *
 * @property error Non-null when an error dialog should be shown. Cleared via
 *           [SettingsViewModel.updateBiometricsErrorState].
 */
data class BiometricsState(
    val error: String? = null,
)
