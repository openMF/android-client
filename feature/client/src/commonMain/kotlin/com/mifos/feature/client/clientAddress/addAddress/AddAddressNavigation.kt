/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddress.addAddress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class AddAddressRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientAddAddressRoute(
    onNavigateBack: () -> Unit,
    navController: NavController,
    onNavigateNext: (Int) -> Unit,
) {
    composable<AddAddressRoute> {
        AddAddressScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientAddAddressRoute(
    id: Int,
) {
    this.navigate(
        AddAddressRoute(id = id),
    ) {
        popUpTo<AddAddressRoute> { inclusive = true }
        launchSingleTop = true
    }
}
