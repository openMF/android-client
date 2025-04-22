/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.activate.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifos.core.common.utils.Constants
import com.mifos.feature.activate.ActivateScreen

/**
 * Created by Pronay Sarker on 18/08/2024 (1:40 PM)
 */
fun NavGraphBuilder.activateScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = ActivateScreens.ActivateScreen.route,
        arguments = listOf(
            navArgument(name = Constants.ACTIVATE_ID, builder = { NavType.IntType }),
            navArgument(name = Constants.ACTIVATE_TYPE, builder = { NavType.StringType }),
        ),
    ) {
        ActivateScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToActivateScreen(id: Int, type: String) {
    navigate(ActivateScreens.ActivateScreen.argument(id, type))
}
