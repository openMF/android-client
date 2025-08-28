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
import com.mifos.feature.note.navigation.Update
import kotlinx.serialization.Serializable

@Serializable
data class NoteRoute(
    val resourceId: Int,
    val resourceType: String?,
)

fun NavGraphBuilder.noteRoute(
    onNavigateBack: () -> Unit,
    onNavigateAddEditNote: (Int, String?, Long?) -> Unit,
) {
    composable<NoteRoute> { updateNoteList ->
        NoteScreenScaffold(
            onNavigateBack = onNavigateBack,
            onNavigateAddEditNote = onNavigateAddEditNote,
            updateNoteList = updateNoteList.savedStateHandle.get<Boolean>(Update.NOTE_LIST),
        )
    }
}

fun NavController.navigateToNoteScreen(entityId: Int, entityType: String?) {
    this.navigate(NoteRoute(entityId, entityType))
}
