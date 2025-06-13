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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifos.core.common.utils.Constants
import com.mifos.feature.document.documentList.DocumentListScreen

/**
 * Created by Pronay Sarker on 17/08/2024 (4:00 AM)
 */
fun NavGraphBuilder.documentListScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = DocumentScreens.DocumentListScreen.route,
        arguments = listOf(
            navArgument(name = Constants.ENTITY_ID, builder = { type = NavType.IntType }),
            navArgument(name = Constants.ENTITY_TYPE, builder = { type = NavType.StringType }),
        ),
    ) {
        DocumentListScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToDocumentListScreen(entityId: Int, entityType: String) {
    navigate(DocumentScreens.DocumentListScreen.argument(entityId, entityType))
}
