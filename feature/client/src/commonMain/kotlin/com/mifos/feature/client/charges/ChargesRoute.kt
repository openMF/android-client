/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.charges

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ChargesRoute(
    val resourceId: Int = -1,
    val resourceType: String = "",
)

fun NavGraphBuilder.chargesDestination(
    onNavigateBack: () -> Unit,
    navController: NavController,
) {
    composable<ChargesRoute> {
        ChargesScreen(
            navigateBack = onNavigateBack,
            navController = navController,
        )
    }
}

fun NavController.navigateToChargesRoute(
    resourceId: Int,
    resourceType: String,
) {
    this.navigate(
        ChargesRoute(
            resourceId = resourceId,
            resourceType = resourceType,
        ),
    )
}
