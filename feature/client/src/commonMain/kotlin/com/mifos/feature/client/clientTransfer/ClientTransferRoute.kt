/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientTransfer

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientTransferRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientTransferDestination(
    onNavigateBack: () -> Unit,
    navController: NavController,
    onNavigateNext: (Int) -> Unit,
) {
    composable<ClientTransferRoute> {
        ClientTransferScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientTransferRoute(
    id: Int,
) {
    this.navigate(
        ClientTransferRoute(
            id = id,
        ),
    )
}
