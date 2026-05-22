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

import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import io.github.vinceglb.filekit.PlatformFile

/**
 * MVI state for the upload/update dialog. The submission lifecycle
 * (Submitting / Submitted / Failed) is exposed as a separate `SubmitState<Unit>`
 * on the ViewModel, not on this state, per the canonical pattern (see
 * `feature/note/AddEditNoteState` + `feature/auth/LoginState`).
 *
 * The dialog runs over an effectively-instant load (we just seed `Content(Unit)`
 * synchronously), so [loadState] is mostly here for shape symmetry with
 * `MutationScreenContent` — the file-picker is the actual "load" step.
 *
 * Field-level validation lives on this state instead of the dialog composable
 * so it survives configuration changes and is testable.
 */
data class UploadDocumentState(
    val entityId: Int = -1,
    val entityType: String = "",

    /** Pre-existing document id when the dialog is in Update mode (else `null`). */
    val documentId: Int? = null,
    val kind: DocumentDialogKind = DocumentDialogKind.Upload,

    val name: String = "",
    val description: String = "",
    val pickedFile: PlatformFile? = null,
    val pickedFileName: String? = null,

    val nameError: Boolean = false,
    val descriptionError: Boolean = false,
    val fileError: Boolean = false,

    val loadState: ScreenState<Unit> = ScreenState.Content(data = Unit, freshness = DataFreshness.FRESH),
) {
    /** True iff every input is non-blank and a file has been picked. */
    val isValid: Boolean
        get() = name.isNotBlank() && description.isNotBlank() && pickedFile != null
}

sealed interface UploadDocumentEvent {
    data object DismissDialog : UploadDocumentEvent

    /**
     * Mutation completed successfully — the list screen should refresh +
     * dismiss the dialog. Emitted from the VM's `submitState` collector in
     * the composable, not from this VM directly (per the canonical
     * `MutationScreenContent.onSubmitted` pattern).
     */
    data object MutationSucceeded : UploadDocumentEvent
}

sealed interface UploadDocumentAction {
    data object DismissDialog : UploadDocumentAction
    data object OpenFilePicker : UploadDocumentAction
    data class NameChanged(val value: String) : UploadDocumentAction
    data class DescriptionChanged(val value: String) : UploadDocumentAction
    data object Submit : UploadDocumentAction

    sealed interface Internal : UploadDocumentAction {
        data class FilePicked(val file: PlatformFile?) : Internal
    }
}
