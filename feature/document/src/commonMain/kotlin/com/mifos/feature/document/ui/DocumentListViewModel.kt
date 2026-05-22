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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.document.DocumentRepository
import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import com.mifos.core.data.store.SubmitState
import com.mifos.core.data.store.submitHandler
import com.mifos.core.ui.store.BaseViewModel
import com.mifos.feature.document.navigation.DocumentListRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Document list ViewModel.
 *
 * Read flow (`getDocuments`) uses direct suspend + try/catch →
 * `ScreenState<List<Document>>` (no Store5 — see [DocumentRepository] doc /
 * RULE-STORE5-FETCH-001 exception for resource-scoped CRUD lists). Re-throws
 * `CancellationException` per structured-concurrency contract
 * (RULE-NO-RUN-CATCHING-001 doesn't permit `runCatching` because it swallows
 * cancellation).
 *
 * Download + remove use independent `submitHandler` / `SubmitState` instances
 * — same pattern as `feature/note/NoteViewModel`'s `deleteSubmit`. Each one
 * auto-reloads the list on success and reset()s itself.
 *
 * Upload + update live in a separate [UploadDocumentViewModel] hosted in a
 * dialog; the list VM owns dialog open/close state via [DocumentListState] and
 * triggers a list reload when the dialog reports success.
 */
class DocumentListViewModel(
    private val repository: DocumentRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DocumentListState, DocumentListEvent, DocumentListAction>(
    initialState = DocumentListState(),
) {
    private val route = savedStateHandle.toRoute<DocumentListRoute>()

    private val downloadSubmit = viewModelScope.submitHandler<Unit>()
    private val removeSubmit = viewModelScope.submitHandler<Unit>()

    val downloadState: StateFlow<SubmitState<Unit>> = downloadSubmit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    val removeState: StateFlow<SubmitState<Unit>> = removeSubmit.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubmitState.Idle,
    )

    init {
        mutableStateFlow.update {
            it.copy(
                entityId = route.entityId,
                entityType = route.entityType,
            )
        }
        loadDocuments(initial = true)

        // After a successful download, the list itself doesn't change, but we
        // refresh defensively (parity with the legacy VM, which called
        // loadDocumentList() in the success branch) and reset the handler so
        // the snackbar can be re-shown on a future download.
        downloadSubmit.state
            .onEach { submit ->
                if (submit is SubmitState.Submitted) {
                    loadDocuments(initial = false)
                    downloadSubmit.reset()
                }
            }
            .launchIn(viewModelScope)

        // After a successful remove, reload the list and reset the handler.
        removeSubmit.state
            .onEach { submit ->
                if (submit is SubmitState.Submitted) {
                    loadDocuments(initial = false)
                    removeSubmit.reset()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onDownloadConsumed() {
        downloadSubmit.reset()
    }

    fun onRemoveConsumed() {
        removeSubmit.reset()
    }

    private fun loadDocuments(initial: Boolean) {
        viewModelScope.launch {
            if (initial) {
                mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
            }
            try {
                val documents = repository.getDocuments(state.entityType, state.entityId)
                val screen = if (documents.isEmpty()) {
                    ScreenState.Empty
                } else {
                    ScreenState.Content(data = documents, freshness = DataFreshness.FRESH)
                }
                mutableStateFlow.update {
                    it.copy(
                        screenState = screen,
                        isRefreshing = false,
                        selectedDocument = null,
                    )
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Error(t),
                        isRefreshing = false,
                    )
                }
            }
        }
    }

    override fun handleAction(action: DocumentListAction) {
        when (action) {
            DocumentListAction.NavigateBack -> sendEvent(DocumentListEvent.NavigateBack)

            DocumentListAction.OnRetry -> loadDocuments(initial = true)

            DocumentListAction.OnRefresh -> {
                mutableStateFlow.update { it.copy(isRefreshing = true) }
                loadDocuments(initial = false)
            }

            DocumentListAction.OnClickAddDocument -> mutableStateFlow.update {
                it.copy(
                    pendingDialog = DocumentDialogKind.Upload,
                    documentBeingEdited = null,
                )
            }

            is DocumentListAction.OnDocumentClicked -> mutableStateFlow.update {
                it.copy(selectedDocument = action.document)
            }

            DocumentListAction.DismissRowActionDialog -> mutableStateFlow.update {
                it.copy(selectedDocument = null)
            }

            DocumentListAction.OnDownloadSelected -> {
                val doc = state.selectedDocument ?: return
                val entityType = state.entityType
                val entityId = state.entityId
                mutableStateFlow.update { it.copy(selectedDocument = null) }
                downloadSubmit.submit {
                    repository.downloadDocument(entityType, entityId, doc.id)
                }
            }

            DocumentListAction.OnUpdateSelected -> {
                val doc = state.selectedDocument ?: return
                mutableStateFlow.update {
                    it.copy(
                        selectedDocument = null,
                        pendingDialog = DocumentDialogKind.Update,
                        documentBeingEdited = doc,
                    )
                }
            }

            DocumentListAction.OnRemoveSelected -> {
                val doc = state.selectedDocument ?: return
                val entityType = state.entityType
                val entityId = state.entityId
                mutableStateFlow.update { it.copy(selectedDocument = null) }
                removeSubmit.submit {
                    repository.removeDocument(entityType, entityId, doc.id)
                }
            }

            DocumentListAction.OnUploadFinished -> {
                mutableStateFlow.update {
                    it.copy(
                        pendingDialog = null,
                        documentBeingEdited = null,
                    )
                }
                loadDocuments(initial = false)
            }

            DocumentListAction.OnUploadDialogDismissed -> mutableStateFlow.update {
                it.copy(
                    pendingDialog = null,
                    documentBeingEdited = null,
                )
            }

            is DocumentListAction.Internal.DocumentsLoaded -> Unit
        }
    }
}
