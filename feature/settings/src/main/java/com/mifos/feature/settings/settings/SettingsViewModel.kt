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
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.DarkThemeConfig
import com.mifos.core.model.MifosAppLanguage
import com.mifos.core.model.ThemeBrand
import com.mifos.core.model.UserData
import com.mifos.feature.settings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefManager: PrefManager,
) : ViewModel() {
    val userData = prefManager.userData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserData.DEFAULT,
    )

    fun changeThemeBrand(themeBrand: ThemeBrand) {
        val updatedUserData = userData.value.copy(themeBrand = themeBrand)
        prefManager.saveUserData(updatedUserData)
    }

    fun changeLanguage(language: MifosAppLanguage) {
        val updatedUserData = userData.value.copy(language = language)
        prefManager.saveUserData(updatedUserData)
    }

    fun changeDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        val updatedUserData = userData.value.copy(darkThemeConfig = darkThemeConfig)
        prefManager.saveUserData(updatedUserData)
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
        icon = MifosIcons.sync,
    ),
    LANGUAGE(
        title = R.string.feature_settings_language,
        details = R.string.feature_settings_language_desc,
        icon = MifosIcons.language,
    ),
    THEME(
        title = R.string.feature_settings_theme,
        details = R.string.feature_settings_theme_desc,
        icon = MifosIcons.theme,
    ),
    PASSCODE(
        title = R.string.feature_settings_change_passcode,
        details = R.string.feature_settings_change_passcode_desc,
        icon = MifosIcons.password,
    ),
    SERVER_CONFIG(
        title = R.string.feature_settings_server_config,
        details = R.string.feature_settings_server_config_desc,
        icon = MifosIcons.Link,
    ),
}
