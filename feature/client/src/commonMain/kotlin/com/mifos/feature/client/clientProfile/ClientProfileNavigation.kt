/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:Suppress("MatchingDeclarationName")

package com.mifos.feature.client.clientProfile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientProfileDestination(
    onNavigateBack: () -> Unit,
    navController: NavController,
    notes: (Int) -> Unit,
    documents: (Int) -> Unit,
    identifiers: (Int) -> Unit,
    navigateToClientDetailsScreen: (Int) -> Unit,
    viewAddress: (Int) -> Unit,
    viewAssociatedAccounts: (Int) -> Unit,
) {
    composable<ClientProfileRoute> {
        ClientProfileScreen(
            notes = notes,
            documents = documents,
            identifiers = identifiers,
            onNavigateBack = onNavigateBack,
            navigateToClientDetailsScreen = navigateToClientDetailsScreen,
            viewAddress = viewAddress,
            viewAssociatedAccounts = viewAssociatedAccounts,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientProfileRoute(id: Int) {
    this.navigate(
        ClientProfileRoute(
            id = id,
        ),
    )
}
