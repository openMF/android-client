/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.settings

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.settings.R
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefManager: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = prefManager
        .settingsInfo
        .map { settings ->
            SettingsUiState(
                tenant = settings.tenant,
                baseUrl = settings.baseUrl,
                passcode = settings.passcode ?: "",
                theme = settings.appTheme,
                language = settings.language,
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.DEFAULT)

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            prefManager.updateTheme(theme)
        }
    }

    fun updateLanguage(language: String): Boolean {
        return (language == MifosAppLanguage.SYSTEM_LANGUAGE.code)
    }

    fun tryUpdatingEndpoint(selectedBaseUrl: String, selectedTenant: String): Boolean {
        val isEndpointUpdated = !(uiState.value.baseUrl == selectedBaseUrl && uiState.value.tenant == selectedTenant)
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
        return !(uiState.value.baseUrl == selectedBaseUrl && uiState.value.tenant == selectedTenant)
    }
}

enum class SettingsCardItem(
    val title: Int,
    val details: Int,
    val icon: ImageVector?,
) {
    SYNC_SURVEY(
        title = R.string.feature_settings_sync_survey,
        details = R.string.feature_settings_sync_survey_desc,
        icon = null,
    ),
    LANGUAGE(
        title = R.string.feature_settings_language,
        details = R.string.feature_settings_language_desc,
        icon = MifosIcons.Language,
    ),
    THEME(
        title = R.string.feature_settings_theme,
        details = R.string.feature_settings_theme_desc,
        icon = MifosIcons.Theme,
    ),
    PASSCODE(
        title = R.string.feature_settings_change_passcode,
        details = R.string.feature_settings_change_passcode_desc,
        icon = MifosIcons.Password,
    ),
    ENDPOINT(
        title = R.string.feature_settings_instance_url,
        details = R.string.feature_settings_instance_url_desc,
        icon = null,
    ),
    SERVER_CONFIG(
        title = R.string.feature_settings_server_config,
        details = R.string.feature_settings_server_config_desc,
        icon = null,
    ),
}

data class SettingsUiState(
    val tenant: String,
    val baseUrl: String,
    val passcode: String,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: MifosAppLanguage = MifosAppLanguage.SYSTEM_LANGUAGE,
) {
    companion object {
        val DEFAULT = SettingsUiState(
            tenant = "",
            baseUrl = "",
            passcode = "",
        )
    }
}
