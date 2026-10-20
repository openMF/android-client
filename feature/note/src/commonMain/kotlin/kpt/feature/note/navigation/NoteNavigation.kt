/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import kpt.core.base.ui.nav.composableWithStayTransitions
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.feature.note.ui.AddEditNoteScreen
import kpt.feature.note.ui.NoteListScreen

/**
 * Notes attached to a client — ported from
 * `6b66e8a43:feature/note/src/commonMain/kotlin/com/mifos/feature/note/navigation/NoteNavigation.kt`.
 *
 * ## Why `clientId` and not `(resourceId, resourceType)`
 *
 * The original routes carried `resourceId: Int` + `resourceType: String?` so one screen could serve
 * clients, groups, loans and savings. MEASURED at `6b66e8a43`: `navigateToNoteScreen` has exactly
 * two call sites, both in `ClientNavigation.kt`, and both pass `Constants.ENTITY_TYPE_CLIENTS`. No
 * other resource type can reach this screen, so the parameter was a hole rather than flexibility —
 * the same shape as the charges route (`ClientChargesRoute`) and the same conclusion.
 *
 * This also makes the migrated substrate a FAITHFUL match rather than a narrowing:
 * `NoteRepository.notesStream(resourceId)` is keyed on a client (`AppStoreIds.ClientNotes`,
 * `NoteEntity.clientId`) and carries no resource type. Had any caller passed a group or a loan, that
 * would have been a real gap to close in `core/store` before this feature could ship.
 *
 * Group and loan notes get their own routes if and when those slices need them.
 *
 * ## Why nothing here is annotated
 *
 * Both destinations are nested drill-downs reached from client detail. Only TOP-LEVEL entries carry
 * `@FeatureDestination` (spec §4); annotating these would register them as their own root entries
 * on the authenticated graph.
 */
@Serializable
data class NoteListRoute(val clientId: Int)

/**
 * Add or edit one note.
 *
 * `noteId == null` means ADD. The original encoded the same thing and then branched on it in four
 * places inside the ViewModel; here it is read once into `editEnabled`.
 */
@Serializable
data class AddEditNoteRoute(val clientId: Int, val noteId: Long?)

fun NavController.navigateToClientNotes(clientId: Int, navOptions: NavOptions? = null) =
    navigate(NoteListRoute(clientId), navOptions)

fun NavController.navigateToAddNote(clientId: Int) =
    navigate(AddEditNoteRoute(clientId, noteId = null))

fun NavController.navigateToEditNote(clientId: Int, noteId: Long) =
    navigate(AddEditNoteRoute(clientId, noteId))

/**
 * Register both note destinations into the caller's graph.
 *
 * The original exposed `noteDestination(navController, onBackPressed)` and `feature/client`
 * depended on `projects.feature.note` to call it. That dependency direction is preserved — client
 * is the hub that navigates into seven sibling feature modules, and reproducing that is what keeps
 * the remaining six slices mechanical.
 *
 * ## The list-refresh contract
 *
 * The original navigated BACK to the list with `popUpTo(inclusive) + launchSingleTop` so the list
 * would re-create and re-fetch after a write. That was necessary because its ViewModel fetched once
 * in `init` and had no way to be told the data changed.
 *
 * Here the list observes a Store-backed stream, so a successful write invalidates the cache and the
 * list updates itself. The add/edit screen therefore just pops — no route gymnastics, and no
 * re-creation of a screen that is already correct.
 */
fun NavGraphBuilder.noteDestinations(navController: NavController) {
    composableWithStayTransitions<NoteListRoute> {
        NoteListScreen(
            onBackClick = navController::popBackStackSafely,
            onAddNote = navController::navigateToAddNote,
            onEditNote = navController::navigateToEditNote,
        )
    }

    composableWithStayTransitions<AddEditNoteRoute> {
        AddEditNoteScreen(onBackClick = navController::popBackStackSafely)
    }
}
