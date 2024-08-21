package com.mifos.feature.checker_inbox_task.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.checker_inbox_task.checker_inbox.CheckerInboxScreen
import com.mifos.feature.checker_inbox_task.checker_inbox_tasks.CheckerInboxTasksScreen

fun NavGraphBuilder.checkerInboxTaskGraph(
    navController: NavController,
) {
    navigation(
        startDestination = CheckerInboxTaskScreens.CheckerInboxTaskScreen.route,
        route = CheckerInboxTaskScreens.CheckerInboxTaskScreenRoute.route
    ) {
        checkerInboxTaskRoute(
            onBackPressed = navController::popBackStack,
            checkerInbox = navController::navigateCheckerInbox
        )
        checkerInboxRoute(
            onBackPressed = navController::popBackStack
        )
    }
}

fun NavGraphBuilder.checkerInboxTaskRoute(
    onBackPressed: () -> Unit,
    checkerInbox: () -> Unit
) {
    composable(
        route = CheckerInboxTaskScreens.CheckerInboxTaskScreen.route
    ) {
        CheckerInboxTasksScreen(
            onBackPressed = onBackPressed,
            checkerInbox = checkerInbox
        )
    }
}

fun NavGraphBuilder.checkerInboxRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = CheckerInboxTaskScreens.CheckerInboxScreen.route
    ) {
        CheckerInboxScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateCheckerInbox() {
    navigate(CheckerInboxTaskScreens.CheckerInboxScreen.route)
}