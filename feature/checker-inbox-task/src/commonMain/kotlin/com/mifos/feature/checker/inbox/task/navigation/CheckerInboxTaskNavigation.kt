/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.checker.inbox.task.checkerInbox.CheckerInboxScreen
import com.mifos.feature.checker.inbox.task.checkerInboxTasks.CheckerInboxTasksScreen
import kotlinx.serialization.Serializable

@Serializable
data object CheckerInboxNavGraph

fun NavGraphBuilder.checkerInboxTaskNavGraph(
    navController: NavController,
) {
    navigation<CheckerInboxNavGraph>(
        startDestination = CheckerInboxTaskScreenRoute,
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

@Serializable
data object CheckerInboxTaskScreenRoute

fun NavGraphBuilder.checkerInboxTaskRoute(
    onBackPressed: () -> Unit,
    checkerInbox: () -> Unit,
) {
    composable<CheckerInboxTaskScreenRoute> {
        CheckerInboxTasksScreen(
            onBackPressed = onBackPressed,
            checkerInbox = checkerInbox,
            onRefresh = checkerInbox,
        )
    }
}

@Serializable
data object CheckerInboxScreenRoute

fun NavGraphBuilder.checkerInboxRoute(
    onBackPressed: () -> Unit,
) {
    composable<CheckerInboxScreenRoute> {
        CheckerInboxScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateCheckerInbox() {
    navigate(CheckerInboxScreenRoute)
}

fun NavController.navigateToCheckerInboxTasksScreenRoute() {
    navigate(CheckerInboxTaskScreenRoute)
}
