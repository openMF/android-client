/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.addEditNotes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class AddEditNoteRoute(
    val resourceId: Int,
    val resourceType: String?,
    val noteId: Long?,
)

fun NavGraphBuilder.addEditNoteRoute(
    onBackPressed: () -> Unit,
    onNavigateWithUpdatedList: (Int, String?) -> Unit,
    navController: NavController,
) {
    composable<AddEditNoteRoute> {
        AddEditNoteScreen(
            onBackPressed = onBackPressed,
            onNavigateWithUpdatedList = onNavigateWithUpdatedList,
            navController = navController,
        )
    }
}

fun NavController.navigateToAddEditNoteScreen(resourceId: Int, resourceType: String?, noteId: Long?) {
    this.navigate(AddEditNoteRoute(resourceId, resourceType, noteId))
}
