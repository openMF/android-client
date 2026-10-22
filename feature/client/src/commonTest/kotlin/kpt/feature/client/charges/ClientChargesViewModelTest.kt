/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.client.charges

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.screen.ExperimentalScreenDataStreamTestingApi
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.screenDataStreamForTesting
import kpt.core.data.charge.ClientChargeRepository
import kpt.core.database.charge.entity.ChargesEntity
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Locks what the port changed about this screen.
 *
 * The original injected no repository at all — its state carried a `chargesFlow` some other screen
 * was meant to populate, plus a hand-rolled `isLoading` and a separate `dialogState`. None of that
 * is assertable, which is why it had no tests.
 */
@OptIn(ExperimentalScreenDataStreamTestingApi::class, ExperimentalCoroutinesApi::class)
class ClientChargesViewModelTest {

    private val charges = MutableStateFlow<ScreenState<List<ChargesEntity>>>(ScreenState.Loading)
    private val refreshes = MutableSharedFlow<Unit>(extraBufferCapacity = 4)
    private val requestedIds = mutableListOf<Int>()

    private val repository = object : ClientChargeRepository {
        override fun clientChargesStream(
            clientId: Int,
            scope: CoroutineScope,
        ): ScreenDataStream<List<ChargesEntity>> {
            requestedIds += clientId
            return screenDataStreamForTesting(charges, refreshTrigger = refreshes)
        }
    }

    private fun viewModel(clientId: Int = 42) =
        ClientChargesViewModel(repository, SavedStateHandle(mapOf("clientId" to clientId)))

    private fun charge(name: String, outstanding: Double?) =
        ChargesEntity(id = 1, name = name, amount = 100.0, amountOutstanding = outstanding)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(Dispatchers.Unconfined)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun theStreamOpensOnTheRouteClientIdNotZero() = runTest {
        viewModel(clientId = 42)
        assertEquals(listOf(42), requestedIds)
    }

    @Test
    fun theHeaderCountsExactlyTheRowsTheListRenders() = runTest {
        // The count moved OUT of the ViewModel when the stream became a StateFlow — the screen now
        // derives it from the same `charges` state it renders (`loadedCount`). The property worth
        // pinning is unchanged and still lives here: ONE source, so the header cannot disagree with
        // the rows beneath it. The original pushed an itemCount up through a setCount callback and
        // could.
        val vm = viewModel()
        charges.value = ScreenState.Content(listOf(charge("Processing fee", 50.0), charge("Late fee", 0.0)))

        vm.charges.test {
            val content = assertIs<ScreenState.Content<List<ChargesEntity>>>(expectMostRecentItem())
            assertEquals(2, content.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aNonContentStateCarriesNoRowsToCount() = runTest {
        // Error and Empty are not Content, so the screen's `loadedCount` falls to 0 rather than
        // holding a stale count from the previous client.
        val vm = viewModel()
        charges.value = ScreenState.Error(RuntimeException("boom"))

        vm.charges.test {
            val failed = expectMostRecentItem()
            assertIs<ScreenState.Error>(failed)
            assertEquals(null, (failed as? ScreenState.Content<List<ChargesEntity>>)?.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun retryReachesTheStreamThatFailed() = runTest {
        val vm = viewModel()
        charges.value = ScreenState.Error(RuntimeException("boom"))

        refreshes.test {
            vm.actionChannel.trySend(ClientChargesAction.Retry)
            // A Retry wired to `{}` would leave this unmet — the reason the stream is held rather
            // than collected inline.
            expectMostRecentItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun backEmitsNavigateBack() = runTest {
        val vm = viewModel()
        vm.eventFlow.test {
            vm.actionChannel.trySend(ClientChargesAction.BackClicked)
            assertEquals(ClientChargesEvent.NavigateBack, expectMostRecentItem())
        }
    }

    @Test
    fun anErrorStaysAnErrorRatherThanBecomingAnEmptyList() = runTest {
        val vm = viewModel()
        charges.value = ScreenState.Error(RuntimeException("502"))

        // This screen is read-only, so a failure that degraded to Empty would be SILENT — the user
        // would read "no charges" for "the server is down". The client does not set expectSuccess,
        // which makes that degradation a live risk rather than a hypothetical one.
        vm.charges.test {
            // `WhileSubscribed` needs a live collector — reading `.value` without one returns the
            // initial value, which is how several assertions here used to pass for the wrong reason.
            assertIs<ScreenState.Error>(expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
