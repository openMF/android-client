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
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettingsScreen() {
    navigate(SettingsRoute)
}

fun NavGraphBuilder.settingsScreen(
    navigateBack: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    onClickUpdateConfig: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(
            onBackPressed = navigateBack,
            navigateToLoginScreen = navigateToLoginScreen,
            changePasscode = changePasscode,
            onClickUpdateConfig = onClickUpdateConfig,
        )
    }
}

@Serializable
data object UpdateServerConfig

fun NavGraphBuilder.serverConfigGraph(
    navigateBack: () -> Unit,
) {
    composable<UpdateServerConfig> {
        UpdateServerConfigScreenRoute(
            onBackClick = navigateBack,
        )
    }
}

fun NavController.navigateToServerConfigGraph() {
    navigate(UpdateServerConfig)
}
