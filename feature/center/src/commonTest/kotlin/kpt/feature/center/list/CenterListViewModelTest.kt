/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.center.list

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.screen.ScreenState
import kpt.feature.center.testing.FakeCenterRepository
import kpt.feature.center.testing.center
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * `CenterListStateTest` covers the derived view (search / filter / sort). This covers the half it
 * cannot reach: the stream subscription and the action dispatch.
 *
 * Both are where the audit found the gap — a state class can be perfect while the ViewModel never
 * subscribes, and every assertion still passes.
 */
class CenterListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun theStreamIsSubscribedOnlyWhileSomethingCollects() = runTest(dispatcher) {
        val repo = FakeCenterRepository()
        val vm = CenterListViewModel(repo)
        repo.emit(center(1), center(2))
        testScheduler.advanceUntilIdle()

        // `WhileSubscribed` means NOTHING flows until a collector attaches — the screen is the
        // collector. Reading `.value` without one yields the initial state, which is exactly the
        // property that stops off-screen tabs holding live refresh loops.
        assertIs<ScreenState.Loading>(vm.screenState.value)

        vm.screenState.test {
            // `stateIn` replays its initial value to every new collector before the upstream
            // starts — so the first item is always Loading, and skipping it is part of the
            // contract rather than a quirk to work around.
            assertIs<ScreenState.Loading>(awaitItem())
            val content = assertIs<ScreenState.Content<List<*>>>(awaitItem())
            assertEquals(2, content.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun theFilterStateIsCombinedIntoTheRenderedRows() = runTest(dispatcher) {
        val repo = FakeCenterRepository()
        val vm = CenterListViewModel(repo)
        repo.emit(center(1, name = "Alpha"), center(2, name = "Beta"))

        vm.screenState.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Content<List<*>>>(awaitItem())
            vm.trySendAction(CenterListAction.QueryChanged("bet"))
            // The combine re-emits on a filter change without re-reading the stream.
            val filtered = assertIs<ScreenState.Content<List<*>>>(awaitItem())
            assertEquals(1, filtered.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aTappedCenterRaisesOpenCenterWithItsId() = runTest(dispatcher) {
        val vm = CenterListViewModel(FakeCenterRepository())
        vm.eventFlow.test {
            vm.trySendAction(CenterListAction.CenterClicked(42))
            assertEquals(CenterListEvent.OpenCenter(42), awaitItem())
        }
    }

    @Test
    fun filterActionsMutateStateAndClearTogether() = runTest(dispatcher) {
        val vm = CenterListViewModel(FakeCenterRepository())

        vm.trySendAction(CenterListAction.QueryChanged("abc"))
        vm.trySendAction(CenterListAction.OfficeFilterToggled("Branch A"))
        vm.trySendAction(CenterListAction.SortChanged(CenterSort.Name))
        testScheduler.advanceUntilIdle()
        assertEquals("abc", vm.stateFlow.value.query)
        assertEquals(setOf("Branch A"), vm.stateFlow.value.selectedOffices)

        vm.trySendAction(CenterListAction.FiltersCleared)
        testScheduler.advanceUntilIdle()
        // Clearing resets query AND offices. The sort deliberately SURVIVES — it is an ordering
        // preference, not a filter, and wiping it would reorder the list under a user who only
        // wanted to widen the search.
        assertEquals("", vm.stateFlow.value.query)
        assertEquals(emptySet(), vm.stateFlow.value.selectedOffices)
        assertEquals(CenterSort.Name, vm.stateFlow.value.sort)
    }

    @Test
    fun theOfficeFilterToggles() = runTest(dispatcher) {
        val vm = CenterListViewModel(FakeCenterRepository())
        vm.trySendAction(CenterListAction.OfficeFilterToggled("A"))
        testScheduler.advanceUntilIdle()
        assertEquals(setOf("A"), vm.stateFlow.value.selectedOffices)

        vm.trySendAction(CenterListAction.OfficeFilterToggled("A"))
        testScheduler.advanceUntilIdle()
        assertEquals(emptySet(), vm.stateFlow.value.selectedOffices)
    }
}
