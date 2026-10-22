/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.groups.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.screen.ScreenState
import kpt.core.database.group.entity.GroupEntity
import kpt.feature.groups.testing.FakeGroupCommandRepository
import kpt.feature.groups.testing.FakeGroupRepository
import kpt.feature.groups.testing.group
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GroupsDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        repo: FakeGroupRepository,
        commands: FakeGroupCommandRepository = FakeGroupCommandRepository(),
        groupId: Int = 7,
    ) = GroupsDetailViewModel(repo, commands, SavedStateHandle(mapOf("groupId" to groupId)))

    @Test
    fun theScreenShowsTheOneGroupItWasOpenedFor() = runTest(dispatcher) {
        val repo = FakeGroupRepository()
        val vm = viewModel(repo)
        repo.emit(group(1), group(7, name = "Target"), group(9))

        vm.group.test {
            assertIs<ScreenState.Loading>(awaitItem())
            val content = assertIs<ScreenState.Content<GroupEntity>>(awaitItem())
            assertEquals("Target", content.data.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aGroupOutsideTheCaseloadIsEmptyNotBlankFields() = runTest(dispatcher) {
        // The distinction that makes the screen honest: a Content holding null would render a
        // detail page of empty rows, which reads as "this group has no data" rather than "this
        // group is not yours".
        val repo = FakeGroupRepository()
        val vm = viewModel(repo)
        repo.emit(group(1), group(2))

        vm.group.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Empty>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun activateIsOfferedOnlyForAnInactiveGroup() = runTest(dispatcher) {
        val repo = FakeGroupRepository()
        val vm = viewModel(repo)

        vm.canActivate.test {
            assertFalse(awaitItem())
            repo.emit(group(7, active = true))
            expectNoEvents()

            repo.emit(group(7, active = false))
            assertTrue(awaitItem(), "an inactive group must offer Activate")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun aGroupWithAnUnknownStatusStillOffersActivation() = runTest(dispatcher) {
        // Fineract leaves `active` NULL for a pending group — the very group an officer opens this
        // screen to activate. Found on device: the screen printed "Inactive" and showed no button,
        // because the status text tested `!= true` and canActivate tested `== false`.
        val repo = FakeGroupRepository()
        val vm = viewModel(repo)
        vm.canActivate.test {
            assertFalse(awaitItem())
            repo.emit(group(7, active = null))
            assertTrue(awaitItem(), "a null status reads as Inactive and must be activatable")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun tappingActivateOpensTheSharedActivateScreenForThisGroup() = runTest(dispatcher) {
        // Activation moved OUT of this ViewModel. It used to write inline with an all-null
        // `ActivatePayload`, assuming the server defaulted the activation date; Fineract's command
        // takes `activationDate` + `dateFormat` + `locale`, and the original has always collected
        // the date on a dedicated screen (`feature/activate`, shared with client and centre). So
        // this screen navigates, and the in-flight guard / offline reporting live with the write.
        val repo = FakeGroupRepository()
        val vm = viewModel(repo, FakeGroupCommandRepository())
        repo.emit(group(7, active = false))
        testScheduler.advanceUntilIdle()

        vm.eventFlow.test {
            vm.trySendAction(GroupsDetailAction.ActivateClicked)
            assertEquals(GroupsDetailEvent.Activate(groupId = 7), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun theActivateButtonSendsNoRequestFromThisScreen() = runTest(dispatcher) {
        val repo = FakeGroupRepository()
        val commands = FakeGroupCommandRepository()
        val vm = viewModel(repo, commands)
        repo.emit(group(7, active = false))
        testScheduler.advanceUntilIdle()

        vm.trySendAction(GroupsDetailAction.ActivateClicked)
        testScheduler.advanceUntilIdle()

        // The tap must NOT post from here — doing so would activate with a date the officer was
        // never asked for, which is the defect this move fixed.
        assertEquals(emptyList(), commands.activations)
    }
}
