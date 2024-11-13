/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checkerInboxTask.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.checkerInboxTask.checkerInbox.CheckerInboxScreen
import com.mifos.feature.checkerInboxTask.checkerInboxTasks.CheckerInboxTasksScreen

fun NavGraphBuilder.checkerInboxTaskGraph(
    navController: NavController,
) {
    navigation(
        startDestination = CheckerInboxTaskScreens.CheckerInboxTaskScreen.route,
        route = CheckerInboxTaskScreens.CheckerInboxTaskScreenRoute.route,
    ) {
        checkerInboxTaskRoute(
            onBackPressed = navController::popBackStack,
            checkerInbox = navController::navigateCheckerInbox,
        )
        checkerInboxRoute(
            onBackPressed = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.checkerInboxTaskRoute(
    onBackPressed: () -> Unit,
    checkerInbox: () -> Unit,
) {
    composable(
        route = CheckerInboxTaskScreens.CheckerInboxTaskScreen.route,
    ) {
        CheckerInboxTasksScreen(
            onBackPressed = onBackPressed,
            checkerInbox = checkerInbox,
        )
    }
}

fun NavGraphBuilder.checkerInboxRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = CheckerInboxTaskScreens.CheckerInboxScreen.route,
    ) {
        CheckerInboxScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateCheckerInbox() {
    navigate(CheckerInboxTaskScreens.CheckerInboxScreen.route)
}
