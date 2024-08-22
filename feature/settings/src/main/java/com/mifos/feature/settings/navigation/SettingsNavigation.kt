package com.mifos.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.settings.settings.SettingsScreen
import com.mifos.feature.settings.updateServer.UpdateServerConfigScreenRoute

/**
 * Created by Pronay Sarker on 10/08/2024 (7:52 AM)
 */
fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    languageChanged: () -> Unit,
) {
    composable(
        route = SettingsScreens.SettingsScreen.route
    ) {
        SettingsScreen(
            onBackPressed = navigateBack,
            navigateToLoginScreen = navigateToLoginScreen,
            languageChanged = languageChanged,
            changePasscode = changePasscode
        )
    }
}