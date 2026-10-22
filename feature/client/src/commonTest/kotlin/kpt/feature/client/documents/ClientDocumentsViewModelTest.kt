/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(kpt.core.base.store.screen.ExperimentalScreenDataStreamTestingApi::class)

package kpt.feature.client.documents

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.screenDataStreamForTesting
import kpt.core.data.document.DocumentCommandRepository
import kpt.core.data.document.DocumentRepository
import kpt.core.model.objects.noncoreobjects.Document
import kpt.core.model.shared.GenericResponse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClientDocumentsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private val docsState = MutableStateFlow<ScreenState<List<Document>>>(ScreenState.Loading)
    private val refreshes = MutableSharedFlow<Unit>(extraBufferCapacity = 8)
    private val requested = mutableListOf<Pair<String, Int>>()

    private val repository = object : DocumentRepository {
        override fun documentsStream(
            entityType: String,
            entityId: Int,
            scope: CoroutineScope,
        ): ScreenDataStream<List<Document>> {
            requested += entityType to entityId
            return screenDataStreamForTesting(docsState, refreshTrigger = refreshes)
        }

        // Not read by this screen — the SIGNATURE screen downloads attachments. Errors rather than
        // returning null so a future regression that calls it here fails loudly instead of looking
        // like a missing file.
        override suspend fun downloadDocument(entityType: String, entityId: Int, documentId: Int) =
            error("downloadDocument is not used by the documents list")
    }

    private class FakeCommands(
        private val gate: CompletableDeferred<Unit>? = null,
        private val result: () -> MutationResult<GenericResponse> =
            { MutationResult.Applied(GenericResponse(), synced = true) },
    ) : DocumentCommandRepository {
        val removed = mutableListOf<Triple<String, Int, Int>>()

        override suspend fun removeDocument(
            entityType: String,
            entityId: Int,
            documentId: Int,
        ): MutationResult<GenericResponse> {
            removed += Triple(entityType, entityId, documentId)
            gate?.await()
            return result()
        }

        override suspend fun createDocument(
            entityType: String,
            entityId: Int,
            request: MultiPartFormDataContent,
        ) = error("not used here")

        override suspend fun updateDocument(
            entityType: String,
            entityId: Int,
            documentId: Int,
            request: MultiPartFormDataContent,
        ) = error("not used here")
    }

    private fun viewModel(commands: FakeCommands = FakeCommands(), clientId: Int = 42) =
        ClientDocumentsViewModel(repository, commands, SavedStateHandle(mapOf("clientId" to clientId)))

    private fun doc(id: Int, name: String, file: String = "f$id.pdf", desc: String = "") =
        Document(id = id, name = name, fileName = file, description = desc)

    // ---- the search filter ----

    @Test
    fun searchMatchesNameFileNameAndDescription() {
        // The original filtered on `name` ALONE, so a document the officer remembers by its FILE
        // name was unfindable while visible on screen. Widened deliberately.
        val d = doc(1, "Passport", file = "scan-2024.pdf", desc = "identity proof")
        assertTrue(d.matches("pass"))
        assertTrue(d.matches("scan-2024"))
        assertTrue(d.matches("identity"))
        assertTrue(!d.matches("mortgage"))
    }

    @Test
    fun aBlankQueryMatchesEverything() {
        assertTrue(doc(1, "Passport").matches(""))
        assertTrue(doc(1, "Passport").matches("   "))
    }

    @Test
    fun theFilterPassesNonContentStatesThrough() {
        // A filter must never turn an Error into an apparently-empty list.
        assertIs<ScreenState.Error>(
            ScreenState.Error(RuntimeException("boom")).applyQuery("anything"),
        )
        assertIs<ScreenState.Loading>(ScreenState.Loading.applyQuery("anything"))
    }

    @Test
    fun theQueryIsAppliedOnSearchNotOnEveryKeystroke() = runTest(dispatcher) {
        val vm = viewModel()
        docsState.value = ScreenState.Content(listOf(doc(1, "Passport"), doc(2, "Deed")))
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(ClientDocumentsAction.UpdateSearchQuery("pass"))
        testScheduler.advanceUntilIdle()

        vm.documents.test {
            // `awaitItem`, not `expectMostRecentItem`: WhileSubscribed means the first item this
            // collector sees is the initial Loading, and Content arrives only once the scheduler
            // runs. Reading "most recent" first gets Loading and passes for the wrong reason.
            assertIs<ScreenState.Loading>(awaitItem())
            // Typing alone does not filter — the original committed on the search button too.
            val typing = assertIs<ScreenState.Content<List<Document>>>(awaitItem())
            assertEquals(2, typing.data.size)

            vm.actionChannel.trySend(ClientDocumentsAction.SearchDocument)
            testScheduler.advanceUntilIdle()
            val filtered = assertIs<ScreenState.Content<List<Document>>>(expectMostRecentItem())
            assertEquals(listOf("Passport"), filtered.data.map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun closingTheSearchBarRestoresTheWholeList() = runTest(dispatcher) {
        // THE REGRESSION. The original overwrote `clientDocuments` with the filtered result, so the
        // unfiltered set was LOST — only a Refresh brought it back.
        val vm = viewModel()
        docsState.value = ScreenState.Content(listOf(doc(1, "Passport"), doc(2, "Deed")))
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(ClientDocumentsAction.ToggleSearch)
        vm.actionChannel.trySend(ClientDocumentsAction.UpdateSearchQuery("pass"))
        vm.actionChannel.trySend(ClientDocumentsAction.SearchDocument)
        testScheduler.advanceUntilIdle()

        vm.documents.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertEquals(
                1,
                assertIs<ScreenState.Content<List<Document>>>(awaitItem()).data.size,
            )

            vm.actionChannel.trySend(ClientDocumentsAction.ToggleSearch)
            testScheduler.advanceUntilIdle()
            assertEquals(
                2,
                assertIs<ScreenState.Content<List<Document>>>(expectMostRecentItem()).data.size,
            )
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals("", vm.stateFlow.value.searchText)
    }

    // ---- the read + the delete ----

    @Test
    fun theReadIsScopedToThisClientAsAClientEntity() = runTest(dispatcher) {
        viewModel(clientId = 7)
        testScheduler.advanceUntilIdle()
        // The documents endpoint is shared across Fineract entity types; "clients" is what scopes it.
        assertEquals(listOf("clients" to 7), requested.toList())
    }

    @Test
    fun aSuccessfulDeleteReREADsTheListSoTheRowDisappears() = runTest(dispatcher) {
        val commands = FakeCommands()
        val vm = viewModel(commands)
        docsState.value = ScreenState.Content(listOf(doc(1, "Passport")))
        testScheduler.advanceUntilIdle()

        refreshes.test {
            vm.actionChannel.trySend(ClientDocumentsAction.ConfirmDeleteDocument(1))
            testScheduler.advanceUntilIdle()

            assertEquals(listOf(Triple("clients", 42, 1)), commands.removed.toList())
            // The command repository is cache-free, so without this the deleted row stays on
            // screen — the exact defect the note list shipped with.
            expectMostRecentItem()
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(vm.stateFlow.value.writeError)
    }

    @Test
    fun aSecondDeleteInsideTheInFlightWindowIsDropped() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val commands = FakeCommands(gate = gate)
        val vm = viewModel(commands)
        testScheduler.advanceUntilIdle()

        vm.actionChannel.trySend(ClientDocumentsAction.ConfirmDeleteDocument(1))
        testScheduler.advanceUntilIdle()
        vm.actionChannel.trySend(ClientDocumentsAction.ConfirmDeleteDocument(1))
        testScheduler.advanceUntilIdle()
        gate.complete(Unit)
        testScheduler.advanceUntilIdle()

        // A second DELETE on a row that is already gone surfaces a spurious 404.
        assertEquals(1, commands.removed.size)
    }

    @Test
    fun anOfflineDeleteIsReportedAndClearsTheInFlightFlag() = runTest(dispatcher) {
        val commands = FakeCommands(result = { MutationResult.Blocked(BlockReason.OFFLINE) })
        val vm = viewModel(commands)

        vm.actionChannel.trySend(ClientDocumentsAction.ConfirmDeleteDocument(1))
        testScheduler.advanceUntilIdle()

        assertEquals(DocumentWriteError.Offline, vm.stateFlow.value.writeError)
        // Must clear, or the row is un-deletable forever with no indication why.
        assertTrue(!vm.stateFlow.value.isDeleting)
    }

    @Test
    fun theDeleteConfirmationNamesTheFileBeingDeleted() = runTest(dispatcher) {
        val vm = viewModel()
        vm.actionChannel.trySend(
            ClientDocumentsAction.DeleteDocument(documentName = "scan-2024.pdf", documentId = 1),
        )
        testScheduler.advanceUntilIdle()

        val dialog = assertIs<ClientDocumentsState.DialogState.ConfirmDeletion>(vm.stateFlow.value.dialogState)
        assertEquals("scan-2024.pdf", dialog.documentName)
        assertEquals(1, dialog.documentId)
    }
}
