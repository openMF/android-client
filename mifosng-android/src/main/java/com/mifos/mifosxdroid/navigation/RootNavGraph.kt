/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.settings.updateServer.UpdateServerConfigScreenRoute
import org.mifos.library.passcode.navigateToPasscodeScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: String,
    onClickLogout: () -> Unit,
    onUpdateConfig: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        route = MifosNavGraph.ROOT_GRAPH,
    ) {
        authNavGraph(
            route = MifosNavGraph.AUTH_GRAPH,
            navigatePasscode = navController::navigateToPasscodeScreen,
            updateServerConfig = navController::navigateToServerConfigGraph,
        )

        serverConfigGraph(
            navigateBack = navController::popBackStack,
            onUpdateConfig = onUpdateConfig,
        )

        passcodeNavGraph(
            navController = navController,
        )

        homeGraph(
            onClickLogout = onClickLogout,
            onUpdateConfig = onUpdateConfig,
        )
    }
}

// Creating a new navigation graph to update server config in login screen
private const val SERVER_CONFIG_ROUTE = "update_server_config"

private fun NavGraphBuilder.serverConfigGraph(
    navigateBack: () -> Unit,
    onUpdateConfig: () -> Unit,
) {
    navigation(
        startDestination = SERVER_CONFIG_ROUTE,
        route = MifosNavGraph.SETTINGS_GRAPH,
    ) {
        composable(
            route = SERVER_CONFIG_ROUTE,
        ) {
            UpdateServerConfigScreenRoute(
                onBackClick = navigateBack,
                onSuccessful = onUpdateConfig,
            )
        }
    }
}

private fun NavController.navigateToServerConfigGraph() {
    navigate(SERVER_CONFIG_ROUTE)
}
