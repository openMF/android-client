/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddDocuments

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


@Serializable
data class ClientAddDocumentRoute(
    val clientId: Int = -1,
    val documentId: Int = -1,
    val documentPath: String = "",
    val entityType: String = "clients",
    val comingFromPreviewScreen: Boolean = false,
    val updateOnServer: Boolean = false,
    val isDocumentRejected: Boolean = true,
)

fun NavGraphBuilder.clientAddDocumentGraphRoute(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToDocumentPreview:(String) -> Unit
) {
    composable<ClientAddDocumentRoute> {

        ClientAddDocumentsScreen(
            navController = navController,
            navigateBack = navigateBack,
            navigateToDocumentPreviewScreen = navigateToDocumentPreview,
        )
    }

}

fun NavController.navigateToClientAddDocumentRoute(
    clientId: Int,
    documentId: Int,
    entityType: String = "clients",
) {
    this.navigate(ClientAddDocumentRoute(clientId, documentId, entityType = entityType))
}


fun NavController.navigateToClientAddDocumentRouteFromPreview(
    documentPath: String = "",
    entityType: String = "clients",
    updateOnServer: Boolean = false,
    comingFromPreviewScreen: Boolean = false,
    isDocumentRejected: Boolean = true,
) {
    this.navigate(
        ClientAddDocumentRoute(
            documentPath = documentPath,
            entityType = entityType,
            updateOnServer = updateOnServer,
            comingFromPreviewScreen = comingFromPreviewScreen,
            isDocumentRejected = isDocumentRejected,
        )
    )
}


