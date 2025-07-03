/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cmp.navigation.App
import cmp.navigation.navigation.NavGraphRoute.AUTH_GRAPH
import cmp.navigation.navigation.NavGraphRoute.MAIN_GRAPH
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.settings.navigation.navigateToServerConfigGraph
import com.mifos.feature.settings.navigation.serverConfigGraph

@Composable
fun RootNavGraph(
    networkMonitor: NetworkMonitor,
    navHostController: NavHostController,
    startDestination: String,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = NavGraphRoute.ROOT_GRAPH,
        modifier = modifier,
    ) {
        authNavGraph(
            route = AUTH_GRAPH,
            navigateHome = { navHostController.navigate(MAIN_GRAPH) },
            navigatePasscode = { },
            updateServerConfig = navHostController::navigateToServerConfigGraph,
        )

        serverConfigGraph(
            navigateBack = navHostController::popBackStack,
        )

        composable(MAIN_GRAPH) {
            App(
                modifier = modifier,
                networkMonitor = networkMonitor,
                onClickLogout = onClickLogout,
                navigateToLogin = navHostController::navigateToLogin,
                onClickUpdateConfig = navHostController::navigateToServerConfigGraph,
            )
        }
    }
}
