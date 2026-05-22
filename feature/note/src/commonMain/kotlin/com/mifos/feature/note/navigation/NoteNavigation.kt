/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.note.ui.AddEditNoteScreen
import com.mifos.feature.note.ui.NoteScreen

fun NavGraphBuilder.noteDestination(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    composable<NoteRoute> {
        NoteScreen(
            onNavigateBack = onBackPressed,
            onNavigateAddEditNote = navController::navigateToAddEditNoteScreen,
            navController = navController,
        )
    }

    composable<AddEditNoteRoute> {
        AddEditNoteScreen(
            onBackPressed = { navController.popBackStack() },
            onNavigateWithUpdatedList = navController::navigateToNoteScreenWithUpdatedList,
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

fun NavController.navigateToAddEditNoteScreen(
    resourceId: Int,
    resourceType: String?,
    noteId: Long?,
) {
    this.navigate(AddEditNoteRoute(resourceId, resourceType, noteId))
}
