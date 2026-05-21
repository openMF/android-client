/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.activate.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.activate.ui.ActivateScreen

fun NavGraphBuilder.activateDestination(
    onBackPressed: () -> Unit,
) {
    composable<ActivateRoute> {
        ActivateScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToActivateRoute(
    id: Int,
    type: String,
) {
    this.navigate(
        ActivateRoute(
            id = id,
            type = type,
        ),
    )
}
