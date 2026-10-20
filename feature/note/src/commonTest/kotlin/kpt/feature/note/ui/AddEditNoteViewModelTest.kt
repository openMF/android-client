/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.note.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ExperimentalScreenDataStreamTestingApi
import kpt.core.base.store.screen.ScreenState
import kpt.core.database.note.entity.NoteEntity
import kpt.feature.note.testing.FakeNoteCommandRepository
import kpt.feature.note.testing.FakeNoteRepository

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Locks what the port of `6b66e8a43:.../addEditNotes/AddEditNoteViewModel.kt` changed. */
@OptIn(ExperimentalScreenDataStreamTestingApi::class, ExperimentalCoroutinesApi::class)
class AddEditNoteViewModelTest {

    private val repository = FakeNoteRepository()

    private fun viewModel(
        clientId: Int = 42,
        noteId: Long? = null,
        outcome: () -> MutationResult<Unit> = { MutationResult.Applied(Unit, synced = true) },
    ): Pair<AddEditNoteViewModel, FakeNoteCommandRepository> {
        val commands = FakeNoteCommandRepository(outcome)
        val args = mutableMapOf<String, Any?>("clientId" to clientId)
        if (noteId != null) args["noteId"] = noteId
        return AddEditNoteViewModel(repository, commands, SavedStateHandle(args)) to commands
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(Dispatchers.Unconfined)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun aMissingNoteIdMeansAdd() = runTest {
        val (vm, _) = viewModel(noteId = null)
        runCurrent()
        assertFalse(vm.stateFlow.value.editEnabled)
    }

    @Test
    fun theEditorSeedsItselfFromTheNoteAlreadyInTheStore() = runTest {
        repository.state.value = ScreenState.Content(listOf(NoteEntity(id = 3, clientId = 42, noteContent = "existing")))
        val (vm, _) = viewModel(noteId = 3)
        runCurrent()

        // The original called `repository.retrieveNote(...)` — a second network round trip for a
        // note the device already had, which also meant the editor could not open offline.
        val state = vm.stateFlow.value
        assertTrue(state.editEnabled)
        assertEquals("existing", state.text)
        assertEquals("existing", state.loadedText)
    }

    @Test
    fun anUntouchedEditorIsNotDirty() = runTest {
        repository.state.value = ScreenState.Content(listOf(NoteEntity(id = 3, clientId = 42, noteContent = "existing")))
        val (vm, _) = viewModel(noteId = 3)
        runCurrent()
        // isDirty is DERIVED from text vs loadedText. The original kept a separate
        // `notesPayloadInitialData` field that each write path had to remember to update.
        assertFalse(vm.stateFlow.value.isDirty)
    }

    @Test
    fun backOnAnUntouchedEditorLeavesWithoutAsking() = runTest {
        val (vm, _) = viewModel()
        vm.eventFlow.test {
            vm.trySendAction(AddEditNoteAction.BackClicked)
            assertEquals(AddEditNoteEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(vm.stateFlow.value.showDiscardConfirmation)
    }

    @Test
    fun backWithUnsavedTextAsksFirst() = runTest {
        val (vm, _) = viewModel()
        vm.trySendAction(AddEditNoteAction.TextChanged("half a thought"))
        vm.trySendAction(AddEditNoteAction.BackClicked)
        runCurrent()
        // Asks ONLY when there is something to lose — which is what keeps the dialog from being
        // noise the user learns to dismiss without reading.
        assertTrue(vm.stateFlow.value.showDiscardConfirmation)
    }

    @Test
    fun aBlankNoteCannotBeSubmitted() = runTest {
        val (vm, commands) = viewModel()
        vm.trySendAction(AddEditNoteAction.TextChanged("   "))
        vm.trySendAction(AddEditNoteAction.Submit)
        runCurrent()
        // The original let it through and the server rejected it — a round trip to be told
        // nothing was typed.
        assertFalse(vm.stateFlow.value.canSubmit)
        assertEquals(emptyList<FakeNoteCommandRepository.Written>(), commands.added)
    }

    @Test
    fun addSendsTheTrimmedTextAndTheClientResourceType() = runTest {
        val (vm, commands) = viewModel(clientId = 42, noteId = null)
        vm.trySendAction(AddEditNoteAction.TextChanged("  visited the shop  "))
        vm.trySendAction(AddEditNoteAction.Submit)
        runCurrent()
        assertEquals(
            listOf(FakeNoteCommandRepository.Written("clients", 42L, text = "visited the shop")),
            commands.added,
        )
    }

    @Test
    fun editSendsAnUpdateRatherThanAnAdd() = runTest {
        repository.state.value = ScreenState.Content(listOf(NoteEntity(id = 7, clientId = 42, noteContent = "old")))
        val (vm, commands) = viewModel(noteId = 7)
        runCurrent()
        vm.trySendAction(AddEditNoteAction.TextChanged("new"))
        vm.trySendAction(AddEditNoteAction.Submit)
        runCurrent()

        assertEquals(emptyList<FakeNoteCommandRepository.Written>(), commands.added)
        assertEquals(
            listOf(FakeNoteCommandRepository.Written("clients", 42L, noteId = 7L, text = "new")),
            commands.updated,
        )
    }

    @Test
    fun aFailedWriteKeepsWhatTheUserTyped() = runTest {
        val (vm, _) = viewModel(outcome = { MutationResult.Blocked(BlockReason.OFFLINE) })
        vm.trySendAction(AddEditNoteAction.TextChanged("do not lose me"))
        vm.trySendAction(AddEditNoteAction.Submit)
        runCurrent()

        // THE ONE THING THAT EXISTS NOWHERE ELSE. The note is not on the server and not in the
        // cache; if the error clears the field it is simply gone.
        val state = vm.stateFlow.value
        assertEquals("do not lose me", state.text)
        assertEquals(NoteWriteError.Offline, state.writeError)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun retryResubmitsWhatIsInTheFieldNotWhatTheServerHas() = runTest {
        repository.state.value = ScreenState.Content(listOf(NoteEntity(id = 7, clientId = 42, noteContent = "server text")))
        var fail = true
        val (vm, commands) = viewModel(
            noteId = 7,
            outcome = {
                if (fail) MutationResult.Blocked(BlockReason.OFFLINE)
                else MutationResult.Applied(Unit, synced = true)
            },
        )
        runCurrent()
        vm.trySendAction(AddEditNoteAction.TextChanged("my edit"))
        vm.trySendAction(AddEditNoteAction.Submit)
        runCurrent()
        fail = false
        vm.trySendAction(AddEditNoteAction.Retry)
        runCurrent()

        // THE RACE THE ORIGINAL HAD: its OnRetry called loadSingleNote() and THEN editNote(), so a
        // retry could submit the SERVER's text over the user's edit. Both sends carry "my edit".
        assertEquals(listOf<String?>("my edit", "my edit"), commands.updated.map { it.text })
    }

    @Test
    fun aDoubleTapSendsOneWrite() = runTest {
        val (vm, commands) = viewModel()
        vm.trySendAction(AddEditNoteAction.TextChanged("once"))
        vm.trySendAction(AddEditNoteAction.Submit)
        vm.trySendAction(AddEditNoteAction.Submit)
        runCurrent()
        assertEquals(1, commands.added.size)
    }
}
