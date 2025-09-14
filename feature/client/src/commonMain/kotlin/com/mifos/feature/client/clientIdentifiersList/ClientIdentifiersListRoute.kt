/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientIdentifiersAddUpdate.Feature
import kotlinx.serialization.Serializable

@Serializable
data class ClientIdentifiersListRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientIdentifiersListDestination(
    addNewClientIdentity: (Int, Feature, String?) -> Unit,
    onBackPress: () -> Unit,
    navController: NavController,
) {
    composable<ClientIdentifiersListRoute> {
        ClientIdentifiersListScreen(
            addNewClientIdentity = addNewClientIdentity,
            onBackPress = onBackPress,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientIdentifiersListScreen(
    clientId: Int,
) {
    this.navigate(ClientIdentifiersListRoute(clientId = clientId))
}

fun NavController.navigateBackToUpdateClientIdentifiersListScreen(
    clientId: Int,
) {
    this.navigate(ClientIdentifiersListRoute(clientId = clientId)) {
        popUpTo(ClientIdentifiersListRoute(clientId = clientId)) { inclusive = true }
        launchSingleTop = true
    }
}
