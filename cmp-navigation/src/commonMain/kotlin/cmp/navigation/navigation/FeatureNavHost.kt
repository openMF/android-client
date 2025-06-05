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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cmp.navigation.AppState
import com.mifos.feature.about.navigation.aboutNavGraph
import com.mifos.feature.activate.navigation.activateScreen
import com.mifos.feature.note.navigation.noteNavGraph
import com.mifos.feature.settings.navigation.navigateToSettingsScreen
import com.mifos.feature.settings.navigation.settingsScreen

const val WELCOME_ROUTE = "home_screen"

@Composable
internal fun FeatureNavHost(
    appState: AppState,
    onClickLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        route = NavGraphRoute.MAIN_GRAPH,
        startDestination = HomeDestinationsScreen.SearchScreen.route,
        navController = appState.navController,
        modifier = modifier,
    ) {
        homeScreen(onClick = { appState.navController.navigateToSettingsScreen() })

        aboutNavGraph(onBackPressed = appState.navController::popBackStack)

        noteNavGraph(onBackPressed = appState.navController::popBackStack)

        activateScreen(onBackPressed = appState.navController::popBackStack)

        settingsScreen(
            navigateBack = appState.navController::popBackStack,
            navigateToLoginScreen = {},
            changePasscode = {},
            languageChanged = {},
        )
    }
}

fun NavGraphBuilder.homeScreen(onClick: () -> Unit) {
    composable(route = HomeDestinationsScreen.SearchScreen.route) {
        WelcomeScreen(onClick)
    }
}

@Composable
fun WelcomeScreen(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Welcome to Mifos", color = Color.Black)
        Button(onClick = onClick) {
            Text("navigate")
        }
    }
}
