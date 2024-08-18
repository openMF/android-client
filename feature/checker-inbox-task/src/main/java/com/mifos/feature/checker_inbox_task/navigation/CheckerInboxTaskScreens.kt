package com.mifos.feature.checker_inbox_task.navigation

sealed class CheckerInboxTaskScreens(val route: String) {

    data object CheckerInboxTaskScreenRoute :
        CheckerInboxTaskScreens("checker_inbox_task_screen_route")

    data object CheckerInboxTaskScreen : CheckerInboxTaskScreens("checker_inbox_task_screen")

    data object CheckerInboxScreen : CheckerInboxTaskScreens("checker_inbox_screen")

}