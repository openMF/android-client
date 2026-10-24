/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.profile.identity

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.common.manager.DispatcherManager
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.auth.AuthCommandRepository
import kpt.core.datastore.prefs.ProjectPreferencesRepositoryImpl
import kpt.core.datastore.prefs.UserPreferencesRepositoryImpl
import kpt.core.model.auth.PostAuthenticationRequest
import kpt.core.model.auth.PostAuthenticationResponse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The sign-out CHOICE. What each choice removes — both halves of the session, and with "clear data"
 * every store — is `AuthCommandRepository.signOut`'s job and is asserted in core/data's
 * `AuthCommandRepositoryTest`; this pins that the ViewModel asks for the option the officer picked,
 * defaults to the safe one, and keeps the officer signed in when the purge fails.
 */
class ProfileViewModelTest {

    @BeforeTest fun setUp() = Dispatchers.setMain(Dispatchers.Unconfined)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private val dispatchers = object : DispatcherManager {
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val main: MainCoroutineDispatcher = Dispatchers.Main
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
        override val appScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    }

    private fun preferences() = run {
        val plain = MapSettings()
        val secure = MapSettings()
        ProjectPreferencesRepositoryImpl(
            delegate = UserPreferencesRepositoryImpl(plain, secure, dispatchers),
            plainSettings = plain,
            secureSettings = secure,
            dispatcher = dispatchers,
        )
    }

    private class FakeAuth(
        private val result: MutationResult<Unit> = MutationResult.Applied(Unit, synced = true),
    ) : AuthCommandRepository {
        val signOuts = mutableListOf<Boolean>()

        override suspend fun signOut(clearLocalData: Boolean): MutationResult<Unit> {
            signOuts += clearLocalData
            return result
        }

        override suspend fun authenticate(postAuthenticationRequest: PostAuthenticationRequest) =
            error("not used by Profile")

        override suspend fun resumeStoredSession(): MutationResult<Unit> = error("not used by Profile")
    }

    @Test
    fun signOutIsConfirmedBeforeItHappens() = runTest {
        val auth = FakeAuth()
        val vm = ProfileViewModel(preferences(), auth)

        vm.trySendAction(ProfileAction.SignOutRequested)
        assertTrue(vm.stateFlow.value.confirmingSignOut)
        vm.trySendAction(ProfileAction.SignOutCancelled)

        assertFalse(vm.stateFlow.value.confirmingSignOut)
        assertEquals(emptyList(), auth.signOuts)
    }

    @Test
    fun theDefaultKeepsTheData() = runTest {
        val auth = FakeAuth()
        val vm = ProfileViewModel(preferences(), auth)

        vm.eventFlow.test {
            vm.trySendAction(ProfileAction.SignOutRequested)
            assertEquals(SignOutMode.SessionOnly, vm.stateFlow.value.signOutMode)
            vm.trySendAction(ProfileAction.SignOutConfirmed)
            assertEquals(ProfileEvent.SignedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(false), auth.signOuts)
    }

    @Test
    fun choosingClearDataAsksForIt() = runTest {
        val auth = FakeAuth()
        val vm = ProfileViewModel(preferences(), auth)

        vm.eventFlow.test {
            vm.trySendAction(ProfileAction.SignOutRequested)
            vm.trySendAction(ProfileAction.SignOutModeSelected(SignOutMode.SessionAndData))
            vm.trySendAction(ProfileAction.SignOutConfirmed)
            assertEquals(ProfileEvent.SignedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(true), auth.signOuts)
    }

    @Test
    fun reopeningTheDialogStartsOnTheSafeChoiceAgain() = runTest {
        val vm = ProfileViewModel(preferences(), FakeAuth())

        vm.trySendAction(ProfileAction.SignOutRequested)
        vm.trySendAction(ProfileAction.SignOutModeSelected(SignOutMode.SessionAndData))
        vm.trySendAction(ProfileAction.SignOutCancelled)
        vm.trySendAction(ProfileAction.SignOutRequested)

        assertEquals(SignOutMode.SessionOnly, vm.stateFlow.value.signOutMode)
    }

    @Test
    fun aFailedPurgeKeepsTheOfficerSignedInAndSaysSo() = runTest {
        val vm = ProfileViewModel(preferences(), FakeAuth(MutationResult.Failed(RuntimeException("disk"), rolledBack = false)))

        vm.eventFlow.test {
            vm.trySendAction(ProfileAction.SignOutRequested)
            vm.trySendAction(ProfileAction.SignOutModeSelected(SignOutMode.SessionAndData))
            vm.trySendAction(ProfileAction.SignOutConfirmed)
            expectNoEvents()
        }
        assertTrue(vm.stateFlow.value.signOutFailed)
        assertTrue(vm.stateFlow.value.confirmingSignOut)
    }
}
