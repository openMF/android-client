/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.groups.list

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.screen.ScreenState
import kpt.feature.groups.testing.FakeGroupRepository
import kpt.feature.groups.testing.group
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
class GroupsListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    @Test
    fun theStreamIsSubscribedOnlyWhileSomethingCollects() = runTest(dispatcher) {
        val repo = FakeGroupRepository()
        val vm = GroupsListViewModel(repo)
        repo.emit(group(1), group(2))
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
        val repo = FakeGroupRepository()
        val vm = GroupsListViewModel(repo)
        repo.emit(group(1, name = "Alpha"), group(2, name = "Beta"))

        vm.screenState.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Content<List<*>>>(awaitItem())
            vm.trySendAction(GroupsListAction.QueryChanged("bet"))
            // The combine re-emits on a filter change without re-reading the stream.
            val filtered = assertIs<ScreenState.Content<List<*>>>(awaitItem())
            assertEquals(1, filtered.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aTappedGroupRaisesOpenGroupWithItsId() = runTest(dispatcher) {
        val vm = GroupsListViewModel(FakeGroupRepository())
        vm.eventFlow.test {
            vm.trySendAction(GroupsListAction.GroupClicked(42))
            assertEquals(GroupsListEvent.OpenGroup(42), awaitItem())
        }
    }

    @Test
    fun filterActionsMutateStateAndClearTogether() = runTest(dispatcher) {
        val vm = GroupsListViewModel(FakeGroupRepository())

        vm.trySendAction(GroupsListAction.QueryChanged("abc"))
        vm.trySendAction(GroupsListAction.OfficeFilterToggled("Branch A"))
        vm.trySendAction(GroupsListAction.SortChanged(GroupsSort.Name))
        testScheduler.advanceUntilIdle()
        assertEquals("abc", vm.stateFlow.value.query)
        assertEquals(setOf("Branch A"), vm.stateFlow.value.selectedOffices)

        vm.trySendAction(GroupsListAction.FiltersCleared)
        testScheduler.advanceUntilIdle()
        // Clearing resets query AND offices. The sort deliberately SURVIVES — it is an ordering
        // preference, not a filter, and wiping it would reorder the list under a user who only
        // wanted to widen the search.
        assertEquals("", vm.stateFlow.value.query)
        assertEquals(emptySet(), vm.stateFlow.value.selectedOffices)
        assertEquals(GroupsSort.Name, vm.stateFlow.value.sort)
    }

    @Test
    fun theOfficeFilterToggles() = runTest(dispatcher) {
        val vm = GroupsListViewModel(FakeGroupRepository())
        vm.trySendAction(GroupsListAction.OfficeFilterToggled("A"))
        testScheduler.advanceUntilIdle()
        assertEquals(setOf("A"), vm.stateFlow.value.selectedOffices)

        vm.trySendAction(GroupsListAction.OfficeFilterToggled("A"))
        testScheduler.advanceUntilIdle()
        assertEquals(emptySet(), vm.stateFlow.value.selectedOffices)
    }
}
