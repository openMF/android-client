/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import cmp.navigation.navigation.MainNavGraph
import cmp.navigation.navigation.RootNavGraph
import cmp.navigation.utils.toObjectNavigationRoute
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.feature.auth.navigation.LoginRoute
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor = koinInject(),
    viewModel: ComposeAppViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val navDestination = when (uiState) {
        is MainUiState.Loading -> LoginRoute.toObjectNavigationRoute()
        is MainUiState.Success -> if ((uiState as MainUiState.Success).isAuthenticated) {
            MainNavGraph.toObjectNavigationRoute()
        } else {
            LoginRoute.toObjectNavigationRoute()
        }
    }

    val isDarkTheme = when (uiState) {
        MainUiState.Loading -> isSystemInDarkTheme()
        is MainUiState.Success -> {
            when ((uiState as MainUiState.Success).appTheme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }
        }
    }
    val useDynamicColors = when (uiState) {
        MainUiState.Loading -> isSystemInDarkTheme()
        is MainUiState.Success -> {
            when ((uiState as MainUiState.Success).appTheme) {
                AppTheme.SYSTEM -> true
                else -> false
            }
        }
    }

    MifosTheme(
        isDarkTheme,
        dynamicColor = useDynamicColors,
    ) {
        RootNavGraph(
            networkMonitor = networkMonitor,
            navHostController = navController,
            startDestination = navDestination,
            onClickLogout = {
                viewModel.logout()
                navController.navigate(LoginRoute) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            modifier = modifier,
        )
    }
}
