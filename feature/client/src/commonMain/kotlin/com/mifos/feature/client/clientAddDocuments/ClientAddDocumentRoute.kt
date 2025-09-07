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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


@Serializable
data class ClientAddDocumentGraphRoute(
    val clientId: Int = -1,
    val documentId: Int = -1,
    val entityType: String = "clients",
    val openInViewMode: Boolean = false,
    val fileName: String = ""
)

@Serializable
data object ClientAddDocumentRoute

@Serializable
data object ClientDocumentPreviewRoute

@Composable
fun NavBackStackEntry.sharedViewModel(
    navController: NavController
): ClientAddDocumentScreenViewmodel {

    val parentEntry = remember(this) {
        navController.getBackStackEntry<ClientAddDocumentGraphRoute>()
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}


fun NavGraphBuilder.clientAddDocumentGraphRoute(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToDocumentPreview:() -> Unit
) {
    navigation<ClientAddDocumentGraphRoute>(startDestination = ClientAddDocumentRoute){

        composable<ClientAddDocumentRoute> {entry ->

            val viewModel = entry.sharedViewModel(navController)

            ClientAddDocumentsScreen(
                navController = navController,
                navigateBack = navigateBack,
                navigateToDocumentPreviewScreen = navigateToDocumentPreview,
                viewModel = viewModel
            )
        }
        composable<ClientDocumentPreviewRoute> {entry ->
            val viewModel = entry.sharedViewModel(navController)

            ClientDocumentPreviewScreen(
                navigateToAddDocumentScreen = navigateBack,
                viewmodel = viewModel
            )
        }
    }
}

fun NavController.navigateToClientAddDocumentGraphRoute(
    clientId: Int,
    documentId: Int,
    entityType: String,
    openInViewMode: Boolean,
    fileName: String = ""
) {
    this.navigate(ClientAddDocumentGraphRoute(clientId, documentId, entityType, openInViewMode, fileName))
}



fun NavController.navigateToClientDocumentPreviewScreen() {
    this.navigate(ClientDocumentPreviewRoute)
}

