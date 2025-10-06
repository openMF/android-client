/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersAddUpdate

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientIdentitiesAddUpdateRoute(
    val clientId: Int,
    val feature: Feature,
    val uniqueKeyForHandleDocument: String?,
)

fun NavGraphBuilder.clientIdentifiersAddUpdateDestination(
    onBackPressed: () -> Unit,
    onUpdatedListBack: (Int) -> Unit,
    navController: NavController,
) {
    composable<ClientIdentitiesAddUpdateRoute> {
        ClientIdentifiersAddUpdateScreen(
            onBackPressed = onBackPressed,
            onUpdatedListBack = onUpdatedListBack,
            navController = navController,
        )
    }
}

fun NavController.onNavigateToClientIdentifiersAddUpdateScreen(
    clientId: Int,
    feature: Feature,
    uniqueKeyForHandleDocument: String?,
) {
    this.navigate(
        ClientIdentitiesAddUpdateRoute(
            clientId,
            feature,
            uniqueKeyForHandleDocument,
        ),
    )
}
