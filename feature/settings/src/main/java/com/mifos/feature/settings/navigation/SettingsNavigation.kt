package com.mifos.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.settings.settings.SettingsScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:52 AM)
 */
const val SETTINGS_SCREEN_ROUTE = "settings_screen_route"

fun NavController.navigateToSettingsScreen() {
    this.navigate(SETTINGS_SCREEN_ROUTE)
}

fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    languageChanged: () -> Unit,
    serverConfig: () -> Unit
) {
    composable(SETTINGS_SCREEN_ROUTE) {
        SettingsScreen(
            onBackPressed = navigateBack,
            navigateToLoginScreen = navigateToLoginScreen,
            languageChanged = languageChanged,
            serverConfig = serverConfig,
            changePasscode = { }
        )
    }
}