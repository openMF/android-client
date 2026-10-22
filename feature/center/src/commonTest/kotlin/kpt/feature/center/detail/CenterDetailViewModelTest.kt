/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.center.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.screen.ScreenState
import kpt.core.database.center.entity.CenterEntity
import kpt.core.database.group.entity.GroupEntity
import kpt.feature.center.testing.FakeCenterRepository
import kpt.feature.center.testing.FakeGroupRepository
import kpt.feature.center.testing.center
import kpt.feature.center.testing.group
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * The detail screen merges what the original split across `centerDetails/` and `centerGroupList/`.
 * These tests pin the two properties that merge has to preserve: the screen is about ONE center,
 * and the two halves fail independently.
 */
class CenterDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        centers: FakeCenterRepository,
        groups: FakeGroupRepository,
        centerId: Int = 7,
    ) = CenterDetailViewModel(centers, groups, SavedStateHandle(mapOf("centerId" to centerId)))

    @Test
    fun theScreenShowsTheOneCenterItWasOpenedFor() = runTest(dispatcher) {
        val centers = FakeCenterRepository()
        val vm = viewModel(centers, FakeGroupRepository())
        centers.emit(center(1), center(7, name = "Target"), center(9))

        vm.center.test {
            assertIs<ScreenState.Loading>(awaitItem())
            val content = assertIs<ScreenState.Content<CenterEntity>>(awaitItem())
            assertEquals("Target", content.data.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aCenterOutsideTheCaseloadIsEmptyNotBlankFields() = runTest(dispatcher) {
        // A Content holding null would render a detail page of empty rows, which reads as "this
        // center has no data" rather than "this center is not yours".
        val centers = FakeCenterRepository()
        val vm = viewModel(centers, FakeGroupRepository())
        centers.emit(center(1), center(2))

        vm.center.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Empty>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onlyTheGroupsOfThisCenterAreListed() = runTest(dispatcher) {
        val groups = FakeGroupRepository()
        val vm = viewModel(FakeCenterRepository(), groups)
        groups.emit(group(1, centerId = 7), group(2, centerId = 99), group(3, centerId = 7))

        vm.groups.test {
            assertIs<ScreenState.Loading>(awaitItem())
            val content = assertIs<ScreenState.Content<List<GroupEntity>>>(awaitItem())
            assertEquals(listOf(1, 3), content.data.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aCenterWithNoGroupsIsEmptyRatherThanAnEmptyList() = runTest(dispatcher) {
        // Empty renders the empty state; a Content of zero rows renders a blank area that looks
        // like a section which failed to draw.
        val groups = FakeGroupRepository()
        val vm = viewModel(FakeCenterRepository(), groups)
        groups.emit(group(1, centerId = 99))

        vm.groups.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Empty>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aFailedGroupsReadDoesNotBlankTheCenterDetails() = runTest(dispatcher) {
        // THE property the merge depends on. The original had these as two screens, so one failing
        // could not affect the other; folding them into one screen only stays honest if their
        // ScreenStates remain independent. The center's fields are already on screen and still
        // true, so a groups failure must not replace them.
        val centers = FakeCenterRepository()
        val groups = FakeGroupRepository()
        val vm = viewModel(centers, groups)
        centers.emit(center(7, name = "Still here"))

        vm.center.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Content<CenterEntity>>(awaitItem())

            groups.state.value = ScreenState.Error(RuntimeException("groups boom"))
            testScheduler.advanceUntilIdle()

            // The center flow must NOT re-emit because groups failed. Any emission here is the
            // coupling this test exists to forbid.
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aFailedCenterReadDoesNotDiscardAlreadyLoadedGroups() = runTest(dispatcher) {
        // The same independence in the other direction — asserted separately because a shared
        // `combine` would pass the first test and fail this one.
        val centers = FakeCenterRepository()
        val groups = FakeGroupRepository()
        val vm = viewModel(centers, groups)
        groups.emit(group(1, centerId = 7))

        vm.groups.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Content<List<GroupEntity>>>(awaitItem())

            centers.state.value = ScreenState.Error(RuntimeException("center boom"))
            testScheduler.advanceUntilIdle()

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
