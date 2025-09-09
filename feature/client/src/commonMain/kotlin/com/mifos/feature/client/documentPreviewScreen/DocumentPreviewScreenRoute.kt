/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DocumentPreviewScreenRoute

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateBack: () -> Unit,
    documentRejected: () -> Unit,
) {
    composable<DocumentPreviewScreenRoute> {
        DocumentPreviewScreen(
            navigateBack = navigateBack,
            documentRejected = documentRejected,
        )
    }
}

fun NavController.navigateToDocumentPreviewRoute() {
    this.navigate(DocumentPreviewScreenRoute)
}
