/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import org.mifos.library.passcode.PASSCODE_SCREEN
import org.mifos.library.passcode.passcodeRoute

internal fun NavGraphBuilder.passcodeNavGraph(navController: NavHostController) {
    navigation(
        route = MifosNavGraph.PASSCODE_GRAPH,
        startDestination = PASSCODE_SCREEN,
    ) {
        passcodeRoute(
            onForgotButton = {
                navController.popBackStack()
                navController.navigate(MifosNavGraph.MAIN_GRAPH)
            },
            onSkipButton = {
                navController.popBackStack()
                navController.navigate(MifosNavGraph.MAIN_GRAPH)
            },
            onPasscodeConfirm = {
                navController.popBackStack()
                navController.navigate(MifosNavGraph.MAIN_GRAPH)
            },
            onPasscodeRejected = {
                navController.popBackStack()
                navController.navigate(MifosNavGraph.MAIN_GRAPH)
            },
        )
    }
}
