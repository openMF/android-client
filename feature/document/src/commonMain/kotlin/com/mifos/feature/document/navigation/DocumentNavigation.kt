/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.document.documentList.DocumentListScreen
import kotlinx.serialization.Serializable

@Serializable
data class DocumentListRoute(
    val entityId: Int,
    val entityType: String,
)

fun NavGraphBuilder.documentListScreen(
    onBackPressed: () -> Unit,
) {
    composable<DocumentListRoute> {
        DocumentListScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToDocumentListScreen(entityId: Int, entityType: String) {
    navigate(DocumentListRoute(entityId, entityType))
}
