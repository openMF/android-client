/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.document.ui.DocumentDialogKind
import com.mifos.feature.document.ui.DocumentListScreen

/**
 * Compose-native NavGraphBuilder extensions for the document feature.
 *
 * Wave 9 (Phase C of store5-adoption) consolidated the document UI into a
 * single list screen that hosts the upload/update mutation inline as a
 * `Dialog` (see `DocumentListScreen` + `UploadDocumentScreen`). There is no
 * standalone upload-screen route — the dialog lifecycle is managed by the
 * list ViewModel's `pendingDialog` MVI flag.
 *
 * Public API preserved across Wave 9 — the canonical entry points are
 * [documentListScreen] + [navigateToDocumentListScreen], spelled exactly as
 * before so existing callers in `cmp-navigation/AuthenticatedNavigation` and
 * `feature/client/ClientNavigation` keep compiling unchanged. Two additional
 * aliases ([documentNavGraph] / [navigateToDocumentList]) match the
 * Wave-9-standard naming convention used by `noteDestination` (`feature/note`)
 * and `pathTrackingRoute` (`feature/pathTracking`) for new call sites.
 */
fun NavGraphBuilder.documentNavGraph(
    onBackPressed: () -> Unit,
) {
    documentListScreen(onBackPressed = onBackPressed)
}

/**
 * Documents-list destination. Public API symbol preserved from the legacy
 * `DocumentListScreen.kt` (which exported `documentListScreen`).
 */
fun NavGraphBuilder.documentListScreen(
    onBackPressed: () -> Unit,
) {
    composable<DocumentListRoute> {
        DocumentListScreen(onBackPressed = onBackPressed)
    }
}

// --- NavController extensions -----------------------------------------------

/**
 * Wave-9 public API spelling preserved (consumers in `cmp-navigation` and
 * `feature/client` use `navigateToDocumentListScreen`).
 */
fun NavController.navigateToDocumentListScreen(entityId: Int, entityType: String) {
    navigate(DocumentListRoute(entityId = entityId, entityType = entityType))
}

/**
 * Task-requested alias of [navigateToDocumentListScreen]. New callers should
 * prefer this shorter form.
 */
fun NavController.navigateToDocumentList(entityId: Int, entityType: String) {
    navigateToDocumentListScreen(entityId = entityId, entityType = entityType)
}

/**
 * Convenience that navigates to the documents list pre-seeded to open the
 * upload/update dialog. The dialog itself is hosted by [DocumentListScreen]
 * inline — there is no separate "upload screen" route — so this helper just
 * forwards to the list. Wave 9 kept this surface intentionally small: callers
 * that want the dialog open immediately can post a one-shot action through the
 * `DocumentListViewModel` themselves; this helper is provided for symmetry
 * with the task contract.
 *
 * @param kind          Reserved for future use; currently ignored because the
 *                      dialog is opened by the user pressing the `+` button or
 *                      tapping a row.
 * @param documentId    Reserved for future use (Update mode). See above.
 */
@Suppress("UnusedParameter")
fun NavController.navigateToUploadDocument(
    entityId: Int,
    entityType: String,
    kind: DocumentDialogKind = DocumentDialogKind.Upload,
    documentId: Int? = null,
) {
    navigateToDocumentListScreen(entityId = entityId, entityType = entityType)
}
