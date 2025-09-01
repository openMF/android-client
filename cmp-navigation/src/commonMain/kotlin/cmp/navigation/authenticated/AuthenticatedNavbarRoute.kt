/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.authenticated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object AuthenticatedNavbarRoute

internal fun NavController.navigateToAuthenticatedNavBar(navOptions: NavOptions? = null) {
    navigate(route = AuthenticatedNavbarRoute, navOptions = navOptions)
}

internal fun NavGraphBuilder.authenticatedNavbarGraph(
    onDrawerItemClick: (String) -> Unit,
    navigateToDocumentScreen: (Int, String) -> Unit,
    navigateToNoteScreen: (Int, String) -> Unit,
) {
    composable<AuthenticatedNavbarRoute> {
        AuthenticatedNavbarNavigationScreen(
            onDrawerItemClick = onDrawerItemClick,
            navigateToDocumentScreen = navigateToDocumentScreen,
            navigateToNoteScreen = navigateToNoteScreen,
        )
    }
}
