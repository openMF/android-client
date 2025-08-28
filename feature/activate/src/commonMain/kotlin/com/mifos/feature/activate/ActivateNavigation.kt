/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.activate

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ActivateRoute(
    val id: Int = -1,
    val type: String = "",
)

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
