/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientAddress.addAddress.AddAddressRoute
import kotlinx.serialization.Serializable

@Serializable
data class ClientAddressRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientAddressNavigation(
    onNavigateBack: () -> Unit,
    navController: NavController,
    navigateToAddAddressForm: (Int) -> Unit,
) {
    composable<ClientAddressRoute> {
        ClientAddressScreen(
            onNavigateBack = onNavigateBack,
            navigateToAddAddressForm = navigateToAddAddressForm,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientAddressRoute(
    id: Int,
) {
    this.navigate(
        ClientAddressRoute(id = id),
    )
}

fun NavController.navigateToClientAddressRouteOnStatus(id: Int) {
    this.navigate(
        ClientAddressRoute(id = id),
    ) {
        popUpTo<AddAddressRoute> { inclusive = true }
        launchSingleTop = true
    }
}
