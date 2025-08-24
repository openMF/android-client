/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.note.notes.NoteScreen

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    navigation<NoteNavigationRoute>(
        startDestination = NoteScreenRoute::class,
    ) {
        noteScreen(
            onNavigateBack = onBackPressed,
            onNavigateNext = navController::navigateToAddNoteScreen,
        )

        addNoteRoute(
            onBackPressed = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.noteScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int, String?) -> Unit,
) {
    composable<NoteScreenRoute> {
        NoteScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
        )
    }
}

fun NavGraphBuilder.addNoteRoute(
    onBackPressed: () -> Unit,
) {
    composable<AddNoteScreenRoute> {
//        AddNoteScreen(
//            onBackPressed = onBackPressed,
//        )
    }
}

fun NavController.navigateToNoteScreen(entityId: Int, entityType: String?) {
    this.navigate(NoteScreenRoute(entityId, entityType))
}

fun NavController.navigateToAddNoteScreen(entityId: Int, entityType: String?) {
    this.navigate(AddNoteScreenRoute(entityId, entityType))
}
