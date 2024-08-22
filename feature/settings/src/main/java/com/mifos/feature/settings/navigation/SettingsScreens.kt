package com.mifos.feature.settings.navigation

/**
 * Created by Pronay Sarker on 22/08/2024 (4:31 PM)
 */
sealed class SettingsScreens(val route: String) {
    data object SettingsScreen : SettingsScreens("settings_screen")
}