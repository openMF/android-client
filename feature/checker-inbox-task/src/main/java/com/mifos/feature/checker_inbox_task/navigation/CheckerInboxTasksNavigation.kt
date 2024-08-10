package com.mifos.feature.checker_inbox_task.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.checker_inbox_task.checker_inbox_tasks.ui.CheckerInboxTasksScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:59 AM)
 */
const val CHECKER_INBOX_TASK_SCREEN_ROUTE = "checker_inbox_task_screen_route"

fun NavController.navigateToCheckerInboxTaskScreen() {
    this.navigate(CHECKER_INBOX_TASK_SCREEN_ROUTE)
}

fun NavGraphBuilder.checkerInboxTasksScreen(
    onBackPressed: () -> Unit,
){
    composable(CHECKER_INBOX_TASK_SCREEN_ROUTE) {
        CheckerInboxTasksScreen(
            onBackPressed = onBackPressed,
            checkerInbox = {}
        )
    }
}