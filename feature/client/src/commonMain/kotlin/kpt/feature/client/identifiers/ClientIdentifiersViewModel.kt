/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.client.identifiers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.client.ClientCommandRepository
import kpt.core.data.client.ClientRepository
import kpt.core.model.objects.noncoreobjects.Identifier

/**
 * The client's identity documents — ported from
 * `6b66e8a43:feature/client/clientIdentifiersList/ClientIdentifiersListViewModel.kt` (334 lines).
 *
 * The original was ALREADY on the `BaseViewModel<State, Event, Action>` triad, so the MVI shape is
 * carried over as-is. What changes is everything underneath it.
 *
 * ## Three things the substrate deletes
 *
 * **1. The manual connectivity branch.** The original opened every load with
 * `networkMonitor.isOnline.collect { status -> when (status) { true -> fetch(); false ->
 * DialogState.NoInternet } }` — a hand-rolled offline check wrapping the read, with its own dialog
 * state. `ScreenState.NoNetwork` is a first-class state of the stream, decided by the same engine
 * for every screen, so the branch and the dialog state both go. This is the third screen in this
 * port where a bespoke connectivity path collapsed into the stream (the client, center and group
 * lists had the `userStatus` variant of it).
 *
 * **2. `DataState` → `ScreenState`.** Loading and Error stop being DIALOG states. The original
 * rendered a loading dialog over the list and an error dialog on failure; both are now states of
 * the content area, which is what `ScreenContent` exists to render. `DialogState` survives with
 * ONE case — the delete confirmation — because that is the only one that is genuinely a dialog.
 *
 * **3. Four use-cases become two repository calls.** `GetDocumentsListUseCase`,
 * `RemoveDocumentUseCase`, `DeleteIdentifierUseCase` and `ClientIdentifiersRepository` collapse to
 * `clientIdentifiersStream` + `deleteClientIdentifier`. `core/domain` is deleted on this substrate
 * (§2 of the migration spec); the use-case layer was a pass-through.
 *
 * ## Deliberately NOT ported in this slice
 *
 * The original's `UploadAgain` / `ViewDocument` / `DeleteDocument` actions route into the DOCUMENT
 * feature (`Feature.ADD_UPDATE_DOCUMENT`, `Feature.VIEW_DOCUMENT`), which is S3d and not yet
 * migrated. They are omitted rather than stubbed, and the screen passes `null` for those callbacks
 * so the menu entries are ABSENT rather than inert (RULE-IMPL-DEAD-CLICKABLE-001). Tracked in
 * CAPABILITY_GAPS; they return with `feature/document`.
 */
internal class ClientIdentifiersViewModel(
    repository: ClientRepository,
    private val commands: ClientCommandRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClientIdentifiersState, ClientIdentifiersEvent, ClientIdentifiersAction>(
    ClientIdentifiersState(),
) {

    /** Matches `ClientIdentifiersRoute.clientId` — type-safe nav keys the handle by property name. */
    private val clientId: Long = (savedStateHandle.get<Int>(CLIENT_ID_KEY) ?: 0).toLong()

    private val stream = repository.clientIdentifiersStream(clientId, viewModelScope)

    /**
     * The client's identity documents. `stateIn(… WhileSubscribed …)`, not an `init{}` hand-lift —
     * see `ClientListViewModel.screenState`.
     */
    val identifiers: StateFlow<ScreenState<List<Identifier>>> = stream.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    override fun handleAction(action: ClientIdentifiersAction) {
        when (action) {
            ClientIdentifiersAction.NavigateBack ->
                sendEvent(ClientIdentifiersEvent.NavigateBack)

            ClientIdentifiersAction.AddIdentifierClicked ->
                sendEvent(ClientIdentifiersEvent.AddIdentifier(clientId))

            is ClientIdentifiersAction.ToggleShowMenu ->
                mutableStateFlow.value = state.copy(
                    currentExpandedItem = action.index,
                    expandIdentity = !state.expandIdentity,
                )

            is ClientIdentifiersAction.ShowDeleteConfirmation ->
                mutableStateFlow.value = state.copy(
                    dialogState = ClientIdentifiersState.DialogState.DeleteConfirmation(action.identifierId),
                )

            ClientIdentifiersAction.CloseDialog ->
                mutableStateFlow.value = state.copy(dialogState = null)

            is ClientIdentifiersAction.ConfirmDelete -> delete(action.identifierId)

            // Retry re-runs the READ. The original had no retry at all — a failed load left its
            // error dialog with only a dismiss, and dismissing returned to an empty list.
            ClientIdentifiersAction.Retry -> stream.retry()
        }
    }

    private fun delete(identifierId: Long) {
        // Guarded like every other write on this substrate: the confirmation dialog closes on tap,
        // but a second tap landing inside the in-flight window would send a second DELETE and
        // surface a spurious 404 for a row that was already removed.
        if (state.isDeleting) return

        mutableStateFlow.value = state.copy(isDeleting = true, dialogState = null, writeError = null)
        viewModelScope.launch {
            when (val result = commands.deleteClientIdentifier(clientId, identifierId)) {
                is MutationResult.Applied -> {
                    mutableStateFlow.value = state.copy(isDeleting = false)
                    // The deleted row is owned by the stream, so the list is refreshed rather than
                    // edited locally. Removing it from a local copy would disagree with the cache
                    // the moment anything else re-read it.
                    stream.refresh(forceFresh = true)
                }

                is MutationResult.Blocked ->
                    mutableStateFlow.value = state.copy(
                        isDeleting = false,
                        writeError = if (result.reason == BlockReason.OFFLINE) {
                            IdentifierWriteError.Offline
                        } else {
                            IdentifierWriteError.Failed
                        },
                    )

                is MutationResult.Failed -> {
                    Logger.e(tag = TAG, throwable = result.cause) { "identifier delete failed" }
                    mutableStateFlow.value =
                        state.copy(isDeleting = false, writeError = IdentifierWriteError.Failed)
                }

                is MutationResult.Conflicted ->
                    mutableStateFlow.value =
                        state.copy(isDeleting = false, writeError = IdentifierWriteError.Failed)
            }
        }
    }

    private companion object {
        const val TAG = "ClientIdentifiers"
        const val CLIENT_ID_KEY = "clientId"

        /** Template default — long enough to survive a rotation or a brief tab switch. */
        const val STATE_TIMEOUT_MS = 5_000L
    }
}

/**
 * Screen state.
 *
 * The rows are NOT here — they live on [ClientIdentifiersViewModel.identifiers], the stream. The
 * original kept a plain `List<Identifier>` beside a `DialogState`, which is what let the screen
 * show an empty list and a loading dialog at once, and an error dialog over stale rows.
 */
internal data class ClientIdentifiersState(
    val currentExpandedItem: Int = -1,
    val expandIdentity: Boolean = false,
    val dialogState: DialogState? = null,
    val isDeleting: Boolean = false,
    val writeError: IdentifierWriteError? = null,
) {
    /**
     * One case, where the original had five. Loading / Error / NoInternet / DeletedSuccessfully all
     * described the READ or the write's progress, which the stream and [isDeleting] now carry. A
     * destructive confirmation is the only thing here that is really a dialog.
     */
    sealed interface DialogState {
        data class DeleteConfirmation(val identifierId: Long) : DialogState
    }
}

internal enum class IdentifierWriteError { Offline, Failed }

internal sealed interface ClientIdentifiersAction {
    data object NavigateBack : ClientIdentifiersAction
    data object AddIdentifierClicked : ClientIdentifiersAction
    data class ToggleShowMenu(val index: Int) : ClientIdentifiersAction
    data class ShowDeleteConfirmation(val identifierId: Long) : ClientIdentifiersAction
    data class ConfirmDelete(val identifierId: Long) : ClientIdentifiersAction
    data object CloseDialog : ClientIdentifiersAction
    data object Retry : ClientIdentifiersAction
}

internal sealed interface ClientIdentifiersEvent {
    data object NavigateBack : ClientIdentifiersEvent
    data class AddIdentifier(val clientId: Long) : ClientIdentifiersEvent
}
