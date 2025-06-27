/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.settings.settings.SettingsScreen
import com.mifos.feature.settings.updateServer.UpdateServerConfigScreenRoute

fun NavController.navigateToSettingsScreen() {
    navigate(SettingsScreens.SettingsScreen.route)
}

fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    onClickUpdateConfig: () -> Unit,
) {
    composable(
        route = SettingsScreens.SettingsScreen.route,
    ) {
        SettingsScreen(
            onBackPressed = navigateBack,
            navigateToLoginScreen = navigateToLoginScreen,
            changePasscode = changePasscode,
            onClickUpdateConfig = onClickUpdateConfig,
        )
    }
}

private const val SERVER_CONFIG_ROUTE = "update_server_config"

fun NavGraphBuilder.serverConfigGraph(
    navigateBack: () -> Unit,
) {
    composable(
        route = SERVER_CONFIG_ROUTE,
    ) {
        UpdateServerConfigScreenRoute(
            onBackClick = navigateBack,
        )
    }
}

fun NavController.navigateToServerConfigGraph() {
    navigate(SERVER_CONFIG_ROUTE)
}
