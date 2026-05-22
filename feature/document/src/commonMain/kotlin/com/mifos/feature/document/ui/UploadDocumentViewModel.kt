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

import androidx.lifecycle.viewModelScope
import com.mifos.core.data.document.DocumentRepository
import template.core.base.store.submit.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.core.ui.util.multipartRequestBody
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Upload / update dialog ViewModel.
 *
 * Submit (POST upload / PUT update) uses [submitHandler] / [SubmitState] —
 * same pattern as `feature/note/AddEditNoteViewModel` and
 * `feature/auth/LoginViewModel`. The dialog renders via `MutationScreenContent`
 * to get the standard progress overlay + result handler.
 *
 * The file-picker side-effect is gated through a single MVI Action
 * (`OpenFilePicker`) — the picker is platform-native (`FileKit`) and runs in
 * `viewModelScope` because the picker call itself is a suspend coroutine. We
 * still re-throw `CancellationException` per RULE-NO-RUN-CATCHING-001.
 *
 * The dialog instance is created with seed parameters (entity id / type +
 * kind + existing document) via `parametersOf(...)` at the Koin call site, so
 * the dialog cleanly owns its scope rather than reading from SavedStateHandle.
 */
class UploadDocumentViewModel(
    private val repository: DocumentRepository,
    entityId: Int,
    entityType: String,
    kind: DocumentDialogKind,
    documentId: Int?,
    initialName: String,
    initialDescription: String,
) : BaseViewModel<UploadDocumentState, UploadDocumentEvent, UploadDocumentAction>(
    initialState = UploadDocumentState(
        entityId = entityId,
        entityType = entityType,
        kind = kind,
        documentId = documentId,
        name = initialName,
        description = initialDescription,
    ),
) {

    private val submit = viewModelScope.submitHandler<Unit>()

    val submitState: StateFlow<SubmitState<Unit>> = submit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    fun onSubmitConsumed() {
        submit.reset()
    }

    override fun handleAction(action: UploadDocumentAction) {
        when (action) {
            UploadDocumentAction.DismissDialog -> sendEvent(UploadDocumentEvent.DismissDialog)

            UploadDocumentAction.OpenFilePicker -> launchFilePicker()

            is UploadDocumentAction.NameChanged -> mutableStateFlow.update {
                it.copy(name = action.value, nameError = false)
            }

            is UploadDocumentAction.DescriptionChanged -> mutableStateFlow.update {
                it.copy(description = action.value, descriptionError = false)
            }

            UploadDocumentAction.Submit -> doSubmit()

            is UploadDocumentAction.Internal.FilePicked -> mutableStateFlow.update {
                it.copy(
                    pickedFile = action.file,
                    pickedFileName = action.file?.name,
                    fileError = if (action.file != null) false else it.fileError,
                )
            }
        }
    }

    private fun launchFilePicker() {
        viewModelScope.launch {
            val picked = try {
                FileKit.openFilePicker(
                    type = FileKitType.File(
                        extensions = SUPPORTED_EXTENSIONS,
                    ),
                )
            } catch (ce: CancellationException) {
                throw ce
            } catch (_: Throwable) {
                // File picker can fail on dismiss; treat as no-file-picked.
                null
            }
            trySendAction(UploadDocumentAction.Internal.FilePicked(picked))
        }
    }

    @OptIn(InternalAPI::class)
    private fun doSubmit() {
        val current = state
        val nameError = current.name.isBlank()
        val descriptionError = current.description.isBlank()
        val file = current.pickedFile
        val fileError = file == null
        if (nameError || descriptionError || fileError) {
            mutableStateFlow.update {
                it.copy(
                    nameError = nameError,
                    descriptionError = descriptionError,
                    fileError = fileError,
                )
            }
            return
        }
        // file is non-null here per the guard above; capture to a local for
        // smart-casts.
        val captured: PlatformFile = file!!
        submit.submit {
            val multipart = multipartRequestBody(
                file = captured.readBytes(),
                name = current.name,
                extension = captured.extension,
                description = current.description,
            )
            when (current.kind) {
                DocumentDialogKind.Upload -> repository.createDocument(
                    entityType = current.entityType,
                    entityId = current.entityId,
                    file = multipart,
                )
                DocumentDialogKind.Update -> repository.updateDocument(
                    entityType = current.entityType,
                    entityId = current.entityId,
                    documentId = current.documentId
                        ?: error("UploadDocumentViewModel in Update mode requires a documentId"),
                    file = multipart,
                )
            }
        }
    }

    private companion object {
        val SUPPORTED_EXTENSIONS = listOf(
            "xls",
            "xlsx",
            "pdf",
            "doc",
            "docx",
            "png",
            "jpeg",
            "jpg",
        )
    }
}
