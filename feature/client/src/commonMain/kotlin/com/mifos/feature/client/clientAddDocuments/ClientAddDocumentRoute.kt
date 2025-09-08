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
    @Serializable val documentState: DocumentState,
    val newDocumentPath: String = "",
    val comingFromPreviewScreen: Boolean = false,
    val updateOnServer: Boolean = false,
    val updatedDocument: Boolean = false,
    val isDocumentRejected: Boolean = true,
)

fun NavGraphBuilder.clientAddDocumentGraphRoute(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToDocumentPreview:(documentState: DocumentState) -> Unit
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
    documentState: DocumentState,
) {
    this.navigate(
        ClientAddDocumentRoute(documentState)
    ){
        this.popUpTo<ClientAddDocumentRoute>()
    }
}


fun NavController.navigateToClientAddDocumentRouteFromPreview(
    documentState: DocumentState,
    documentPath: String = "",
    updateOnServer: Boolean = false,
    comingFromPreviewScreen: Boolean = false,
    isDocumentRejected: Boolean = true,
) {
    this.navigate(
        ClientAddDocumentRoute(
            documentState =  documentState,
            newDocumentPath = documentPath,
            updateOnServer = updateOnServer,
            comingFromPreviewScreen = comingFromPreviewScreen,
            isDocumentRejected = isDocumentRejected,
        ),
    ){
        this.popUpTo<ClientAddDocumentRoute>()
    }
}



@Serializable
data class DocumentState(
    val clientId: Int = -1,
    val documentId: Int = -1,
    val entityType: String = "clients",
    val documentPath: String = "",
)