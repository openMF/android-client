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
data object AddDocumentRoute

fun NavGraphBuilder.clientAddDocumentGraphRoute(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToDocumentPreview: () -> Unit,
) {
    composable<AddDocumentRoute> {
        ClientAddDocumentsScreen(
            navController = navController,
            navigateBack = navigateBack,
            navigateToDocumentPreviewScreen = navigateToDocumentPreview,
        )
    }
}

fun NavController.navigateToClientAddDocumentRoute() {
    this.navigate(AddDocumentRoute) {
        this.popUpTo<AddDocumentRoute>()
    }
}
