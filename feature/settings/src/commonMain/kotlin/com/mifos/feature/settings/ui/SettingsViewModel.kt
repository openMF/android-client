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
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.data.repository.UserVerificationRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.datastore.model.DarkThemeConfig
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.store.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import org.mifos.authenticator.passcode.PasscodeManager

/**
 * `SavedStateHandle` key used by the Settings screen to receive the result of
 * the disable-biometrics passcode-verification round trip.
 *
 * Flow: Settings emits [SettingsEvent.RequestDisableBiometrics] →
 * navigation routes to internal passcode → passcode writes `true` on verified
 * or `false` on cancel/fail → SettingsScreen reads the key, dispatches
 * [SettingsAction.DisableBiometricsResult], and clears the key.
 */
const val DISABLE_BIOMETRICS_VERIFICATION_KEY = "com.mifos.authentication.verification.key"

private const val DEFAULT_USER_ID = "default_user"
private const val DEFAULT_USER_EMAIL = "default@mifos.org"
private const val DEFAULT_DISPLAY_NAME = "Mifos User"

/**
 * Settings list ViewModel.
 *
 * Pure-local screen: every value mirrored here comes from
 * `prefManager.settingsInfo` (datastore) — no HTTP. There is no `ScreenState`
 * because there is no "loading" or "error" state for a local-only screen.
 *
 * Biometrics registration / unregistration delegates to a
 * `PlatformAuthenticationProvider` supplied by the screen at action-dispatch
 * time (it's composition-scoped). Disable-biometrics is a two-step round trip
 * through the internal passcode screen — the resulting boolean comes back
 * through the SavedStateHandle channel and is dispatched as
 * [SettingsAction.DisableBiometricsResult].
 *
 * RULE-NO-RUN-CATCHING-001 — no `runCatching`. Direct try/catch with
 * `CancellationException` re-throw where needed.
 */
class SettingsViewModel(
    private val prefManager: UserPreferencesRepository,
    private val passcodeManager: PasscodeManager,
    private val userVerificationRepository: UserVerificationRepository,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsAction>(
    initialState = SettingsState(),
) {

    init {
        // Mirror datastore settings into MVI state. This is a single source of
        // truth — the screen reads from `state`, not from prefManager directly.
        prefManager.settingsInfo
            .onEach { settings ->
                mutableStateFlow.update {
                    it.copy(
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
            }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: SettingsAction) {
        when (action) {
            SettingsAction.NavigateBack -> sendEvent(SettingsEvent.NavigateBack)

            SettingsAction.OnClickSyncSurvey -> mutableStateFlow.update {
                it.copy(showSyncSurveyDialog = true)
            }

            SettingsAction.OnClickLanguage -> mutableStateFlow.update {
                it.copy(showLanguageDialog = true)
            }

            SettingsAction.OnClickTheme -> mutableStateFlow.update {
                it.copy(showThemeDialog = true)
            }

            SettingsAction.OnClickChangePasscode -> {
                passcodeManager.changePasscode()
                sendEvent(SettingsEvent.NavigateToChangePasscode)
            }

            SettingsAction.OnClickBiometrics -> {
                // The screen decides whether to register or disable based on
                // the live `isRegistered` flow from the library — it dispatches
                // either RegisterBiometrics or RequestDisableBiometrics. This
                // branch is unused; kept as a safety no-op so the action is
                // exhaustive.
            }

            SettingsAction.OnClickEndpoint -> mutableStateFlow.update {
                it.copy(showEndpointDialog = true)
            }

            SettingsAction.OnClickServerConfig -> sendEvent(SettingsEvent.NavigateToUpdateServerConfig)

            SettingsAction.DismissSyncSurveyDialog -> mutableStateFlow.update {
                it.copy(showSyncSurveyDialog = false)
            }

            SettingsAction.DismissLanguageDialog -> mutableStateFlow.update {
                it.copy(showLanguageDialog = false)
            }

            SettingsAction.DismissThemeDialog -> mutableStateFlow.update {
                it.copy(showThemeDialog = false)
            }

            SettingsAction.DismissEndpointDialog -> mutableStateFlow.update {
                it.copy(showEndpointDialog = false)
            }

            SettingsAction.DismissBiometricsError -> mutableStateFlow.update {
                it.copy(biometricsError = null)
            }

            is SettingsAction.UpdateTheme -> updateTheme(action.theme)

            is SettingsAction.UpdateLanguage -> {
                val isSystem = action.language.code == MifosAppLanguage.SYSTEM_LANGUAGE.code
                sendEvent(
                    SettingsEvent.UpdateLocale(
                        languageCode = action.language.code,
                        isSystemLanguage = isSystem,
                    ),
                )
            }

            is SettingsAction.UpdateEndpoint -> updateEndpoint(action.baseUrl, action.tenant)

            is SettingsAction.RegisterBiometrics -> registerBiometrics(action.provider)

            SettingsAction.RequestDisableBiometrics -> {
                sendEvent(
                    SettingsEvent.RequestDisableBiometrics(
                        verificationKey = DISABLE_BIOMETRICS_VERIFICATION_KEY,
                    ),
                )
            }

            is SettingsAction.DisableBiometricsResult ->
                onDisableBiometricsResult(action.success, action.provider)
        }
    }

    private fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            prefManager.updateTheme(
                when (theme) {
                    AppTheme.LIGHT -> DarkThemeConfig.LIGHT
                    AppTheme.DARK -> DarkThemeConfig.DARK
                    AppTheme.SYSTEM -> DarkThemeConfig.FOLLOW_SYSTEM
                },
            )
            mutableStateFlow.update { it.copy(showThemeDialog = false) }
        }
    }

    private fun updateEndpoint(selectedBaseUrl: String, selectedTenant: String) {
        viewModelScope.launch {
            val current = state
            val isEndpointUpdated =
                !(current.baseUrl == selectedBaseUrl && current.tenant == selectedTenant)
            mutableStateFlow.update { it.copy(showEndpointDialog = false) }
            if (isEndpointUpdated) {
                try {
                    prefManager.updateSettings(
                        prefManager.settingsInfo.first().copy(
                            baseUrl = selectedBaseUrl,
                            tenant = selectedTenant,
                        ),
                    )
                    sendEvent(SettingsEvent.NavigateToLogin)
                } catch (ce: CancellationException) {
                    throw ce
                } catch (_: Throwable) {
                    // settings write surface — datastore failures are extremely
                    // rare and there's no UI affordance to retry. Swallow
                    // silently to preserve legacy behaviour.
                }
            }
        }
    }

    /**
     * Consumes the disable-biometrics passcode-verification result.
     *
     * On `true` (verified): consumes the one-shot
     * [UserVerificationRepository] token and, iff the token was still valid,
     * calls `authProvider.unregister()`. If the token had expired (30 s
     * window), surfaces an error dialog so the user can retry.
     *
     * On `false` (cancelled, backed-out, or wrong passcode): no-op.
     */
    private fun onDisableBiometricsResult(
        success: Boolean,
        authProvider: PlatformAuthenticationProvider,
    ) {
        if (!success) return
        viewModelScope.launch {
            try {
                if (userVerificationRepository.consumeVerification()) {
                    authProvider.unregister()
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            biometricsError = getString(
                                Res.string.feature_settings_verification_expired,
                            ),
                        )
                    }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                mutableStateFlow.update {
                    it.copy(biometricsError = t.message)
                }
            }
        }
    }

    /**
     * Registers the user's biometrics with the platform authenticator. Called
     * directly when the "Enable Biometrics" button is tapped; no passcode
     * pre-verification is required (the user already authenticated to reach
     * Settings).
     */
    private fun registerBiometrics(systemAuthProvider: PlatformAuthenticationProvider) {
        viewModelScope.launch {
            try {
                val result = systemAuthProvider.registerUser(
                    userName = DEFAULT_USER_ID,
                    emailId = DEFAULT_USER_EMAIL,
                    displayName = DEFAULT_DISPLAY_NAME,
                )
                when (result) {
                    is RegistrationResult.Success -> Unit
                    RegistrationResult.PlatformAuthenticatorNotSet -> {
                        mutableStateFlow.update {
                            it.copy(
                                biometricsError = getString(
                                    Res.string.feature_settings_biometrics_not_set,
                                ),
                            )
                        }
                    }
                    RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                        mutableStateFlow.update {
                            it.copy(
                                biometricsError = getString(
                                    Res.string.feature_settings_biometrics_not_available,
                                ),
                            )
                        }
                    }
                    is RegistrationResult.Error -> {
                        mutableStateFlow.update { it.copy(biometricsError = result.message) }
                    }
                    RegistrationResult.UserCancelled -> Unit
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                mutableStateFlow.update { it.copy(biometricsError = t.message) }
            }
        }
    }
}

/**
 * Settings card vocabulary. The screen iterates over [SettingsCardItem.entries]
 * to build the list — the biometrics row swaps its title at the call site based
 * on `isBiometricsRegistered`.
 */
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
