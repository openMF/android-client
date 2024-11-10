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

sealed class CheckerInboxTaskScreens(val route: String) {

    data object CheckerInboxTaskScreenRoute :
        CheckerInboxTaskScreens("checker_inbox_task_screen_route")

    data object CheckerInboxTaskScreen : CheckerInboxTaskScreens("checker_inbox_task_screen")

    data object CheckerInboxScreen : CheckerInboxTaskScreens("checker_inbox_screen")
}
