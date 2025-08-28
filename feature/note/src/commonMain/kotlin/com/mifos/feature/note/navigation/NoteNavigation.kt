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
import androidx.navigation.navigation
import com.mifos.feature.note.addEditNotes.addEditNoteRoute
import com.mifos.feature.note.addEditNotes.navigateToAddEditNoteScreen
import com.mifos.feature.note.notes.NoteRoute
import com.mifos.feature.note.notes.noteRoute
import kotlinx.serialization.Serializable

@Serializable
object NoteNavigationRoute

object Update {
    const val NOTE_LIST = "update note list"
}

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    navigation<NoteNavigationRoute>(
        startDestination = NoteRoute::class,
    ) {
        noteRoute(
            onNavigateBack = onBackPressed,
            onNavigateAddEditNote = navController::navigateToAddEditNoteScreen,
        )

        addEditNoteRoute(
            onBackPressed = { updateNoteList ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(Update.NOTE_LIST, updateNoteList)
                navController.popBackStack()
            },
        )
    }
}
