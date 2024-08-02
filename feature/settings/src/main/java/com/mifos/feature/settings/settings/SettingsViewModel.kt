package com.mifos.feature.settings.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.common.utils.Constants
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.settings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefManager: PrefManager
) : ViewModel() {

    val tenant: StateFlow<String?> = prefManager.getStringValue(Constants.TENANT)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val baseUrl: StateFlow<String?> = prefManager.getStringValue(Constants.BASE_URL)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val passcode: StateFlow<String?> = prefManager.getStringValue(Constants.PASSCODE)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val theme: StateFlow<String?> = prefManager.getStringValue(Constants.THEME)
        .stateIn(viewModelScope, SharingStarted.Eagerly, "System Theme")

    val language: StateFlow<String?> = prefManager.getStringValue(Constants.LANGUAGE)
        .stateIn(viewModelScope, SharingStarted.Eagerly, "System Language")


    fun updateTheme(theme: AppTheme) {
        prefManager.setStringValue(Constants.THEME, theme.themeName)
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun updateLanguage(language: String): Boolean {
        prefManager.setStringValue(Constants.LANGUAGE, language)
        val isSystemLanguage = (language == MifosAppLanguage.SYSTEM_LANGUAGE.code)
        return isSystemLanguage
    }

    fun tryUpdatingEndpoint(selectedBaseUrl: String, selectedTenant: String): Boolean {
        // TODO Implement endpoint update
        return !(baseUrl.equals(selectedBaseUrl) && tenant.equals(selectedTenant))
    }

}


enum class SettingsCardItem(
    val title: Int,
    val details: Int,
    val icon: ImageVector?
) {
    SYNC_SURVEY(
        title = R.string.feature_settings_sync_survey,
        details = R.string.feature_settings_sync_survey_desc,
        icon = null
    ),
    LANGUAGE(
        title = R.string.feature_settings_language,
        details = R.string.feature_settings_language_desc,
        icon = MifosIcons.language
    ),
    THEME(
        title = R.string.feature_settings_theme,
        details = R.string.feature_settings_theme_desc,
        icon = MifosIcons.theme
    ),
    PASSCODE(
        title = R.string.feature_settings_change_passcode,
        details = R.string.feature_settings_change_passcode_desc,
        icon = MifosIcons.password
    ),
    ENDPOINT(
        title = R.string.feature_settings_instance_url,
        details = R.string.feature_settings_instance_url_desc,
        icon = null
    ),
    SERVER_CONFIG(
        title = R.string.feature_settings_server_config,
        details = R.string.feature_settings_server_config_desc,
        icon = null
    )
}

enum class AppTheme(
    val themeName: String
) {
    SYSTEM(themeName = "System Theme"),
    LIGHT(themeName = "Light Theme"),
    DARK(themeName = "Dark Theme")
}