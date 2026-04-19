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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
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

    fun disableBiometrics(
        authenticationSuccess: Boolean,
        authProvider: PlatformAuthenticationProvider,
    ) {
        viewModelScope.launch {
            when (authenticationSuccess) {
                true -> {
                    if (userVerificationRepository.consumeVerification()) {
                        authProvider.unregister()
                    }
                    Logger.e("Disable-Biometrics") { "Auth Result: $authenticationSuccess" }
                    savedStateHandle.remove<Boolean?>(DISABLE_BIOMETRICS_VERIFICATION_KEY)
                }
                false -> {
                    Logger.e("Disable-Biometrics") { "Auth Result: $authenticationSuccess" }
                    savedStateHandle.remove<Boolean?>(DISABLE_BIOMETRICS_VERIFICATION_KEY)
                }
                null -> {
                    Logger.e("Disable-Biometrics") { "Auth Result: $authenticationSuccess" }
                }
            }
        }
    }

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

    fun changePasscode() {
        passcodeManager.changePasscode()
    }

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

data class BiometricsState(
    val error: String? = null,
)
