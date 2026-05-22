/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.ui

import com.mifos.core.data.store.ScreenState
import com.mifos.core.model.objects.noncoreobjects.Document

/**
 * MVI state for the documents list screen. The documents-list read flow lives
 * in [screenState] (`ScreenState<List<Document>>`); refresh state + the
 * row-action dialog (download / update / remove) live in this top-level
 * state.
 *
 * Document download / remove are mutations — the VM exposes separate
 * `SubmitState<Unit>` for each (see [DocumentListViewModel.downloadState] /
 * `removeState`). This [DocumentListState] carries only the read + per-row UI
 * bits.
 *
 * Upload + update are owned by a separate `UploadDocumentState` /
 * `UploadDocumentViewModel` displayed in a `Dialog`. The list screen drives
 * the dialog open/close + tells it whether the operation is `Upload` or
 * `Update` via [pendingDialog].
 */
data class DocumentListState(
    val entityId: Int = -1,
    val entityType: String = "",
    val screenState: ScreenState<List<Document>> = ScreenState.Loading,
    val isRefreshing: Boolean = false,

    /** Document tapped in the list — opens the row-action dialog. */
    val selectedDocument: Document? = null,

    /** When non-null, the upload/update dialog is shown for this action. */
    val pendingDialog: DocumentDialogKind? = null,

    /** Pre-populated when the user picks `Update` on an existing document. */
    val documentBeingEdited: Document? = null,
)

/**
 * Discriminates the two modes the file-picker dialog can render in.
 *
 * - [Upload] — fresh upload; the dialog seeds blank name / description fields.
 * - [Update] — replace an existing document; the dialog seeds with the
 *   document's existing name + description.
 */
enum class DocumentDialogKind { Upload, Update }

sealed interface DocumentListEvent {
    data object NavigateBack : DocumentListEvent
}

sealed interface DocumentListAction {
    data object NavigateBack : DocumentListAction
    data object OnRetry : DocumentListAction
    data object OnRefresh : DocumentListAction

    /** User tapped the `+` action — open the upload dialog. */
    data object OnClickAddDocument : DocumentListAction

    /** User tapped a list row — open the row-action picker. */
    data class OnDocumentClicked(val document: Document) : DocumentListAction

    /** User dismissed the row-action picker. */
    data object DismissRowActionDialog : DocumentListAction

    /** User picked Download from the row-action picker. */
    data object OnDownloadSelected : DocumentListAction

    /** User picked Update from the row-action picker — open the upload dialog in Update mode. */
    data object OnUpdateSelected : DocumentListAction

    /** User picked Remove from the row-action picker. */
    data object OnRemoveSelected : DocumentListAction

    /** Upload/update dialog reported success — close + refresh list. */
    data object OnUploadFinished : DocumentListAction

    /** Upload/update dialog reported close-without-success. */
    data object OnUploadDialogDismissed : DocumentListAction

    sealed interface Internal : DocumentListAction {
        data object DocumentsLoaded : Internal
    }
}
