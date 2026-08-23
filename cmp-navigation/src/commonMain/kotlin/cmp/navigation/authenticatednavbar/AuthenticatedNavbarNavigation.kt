/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("MatchingDeclarationName")

package cmp.navigation.authenticatednavbar

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import kpt.core.base.ui.nav.composableWithStayTransitions

@Serializable
data object AuthenticatedNavbarRoute

internal fun NavController.navigateToAuthenticatedNavBar(navOptions: NavOptions? = null) {
    navigate(route = AuthenticatedNavbarRoute, navOptions = navOptions)
}

internal fun NavGraphBuilder.authenticatedNavbarGraph(
    navigateToSettingsScreen: () -> Unit,
    homeBody: @Composable () -> Unit,
) {
    composableWithStayTransitions<AuthenticatedNavbarRoute> {
        AuthenticatedNavbarNavigationScreen(
            navigateToSettingsScreen = navigateToSettingsScreen,
            homeBody = homeBody,
        )
    }
}
