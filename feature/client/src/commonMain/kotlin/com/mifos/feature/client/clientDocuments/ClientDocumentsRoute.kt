/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDocuments

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientDocumentsRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientDocumentsDestination(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToAddDocuments: () -> Unit,
    onViewDocument: () -> Unit,
) {
    composable<ClientDocumentsRoute> {
        ClientDocumentScreen(
            navController = navController,
            onNavigateBack = navigateBack,
            onViewDocument = onViewDocument,
            onNavigateToAddDocument = navigateToAddDocuments,
        )
    }
}

fun NavController.navigateToClientDocumentsRoute(
    clientId: Int,
) {
    this.navigate(ClientDocumentsRoute(clientId = clientId)) {
        this.popUpTo<ClientDocumentsRoute>()
    }
}
