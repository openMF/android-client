/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.notes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class NoteRoute(
    val resourceId: Int,
    val resourceType: String?,
)

fun NavGraphBuilder.noteRoute(
    onNavigateBack: () -> Unit,
    onNavigateAddEditNote: (Int, String?, Long?) -> Unit,
    navController: NavController,
) {
    composable<NoteRoute> {
        NoteScreen(
            onNavigateBack = onNavigateBack,
            onNavigateAddEditNote = onNavigateAddEditNote,
            navController = navController,
        )
    }
}

fun NavController.navigateToNoteScreen(entityId: Int, entityType: String?) {
    this.navigate(NoteRoute(entityId, entityType))
}

fun NavController.navigateToNoteScreenWithUpdatedList(entityId: Int, entityType: String?) {
    this.navigate(NoteRoute(entityId, entityType)) {
        popUpTo(NoteRoute(entityId, entityType)) { inclusive = true }
        launchSingleTop = true
    }
}
