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
import kpt.core.datastore.prefs.ProjectPreferencesRepository
import kpt.core.datastore.prefs.ProjectPreferencesRepositoryImpl
import kpt.core.datastore.prefs.UserPreferencesRepository
import kpt.core.datastore.prefs.UserPreferencesRepositoryImpl
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Profile is a NEW surface rather than a port — the field-officer app had no Profile tab — so the
 * thing worth pinning is not fidelity to an original but the two decisions recorded in
 * `feature/profile/MIGRATION.md` §4: sign-out works OFFLINE, and it clears BOTH halves of the
 * session.
 *
 * Uses the REAL preference repositories over in-memory `MapSettings`, as `SignInViewModelTest`
 * does. A hand-written fake would be stubs of an interface that inherits 22 framework members, and
 * would prove only that the stubs agree with themselves.
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

    private class Fixture(
        val project: ProjectPreferencesRepository,
        val user: UserPreferencesRepository,
    )

    private fun fixture(): Fixture {
        val plain = MapSettings()
        val secure = MapSettings()
        val user = UserPreferencesRepositoryImpl(plain, secure, dispatchers)
        return Fixture(
            project = ProjectPreferencesRepositoryImpl(
                delegate = user,
                plainSettings = plain,
                secureSettings = secure,
                dispatcher = dispatchers,
            ),
            user = user,
        )
    }

    @Test
    fun signOutIsConfirmedBeforeItHappens() = runTest {
        // A destructive action on the account surface. Requesting it must not end the session —
        // only confirming does.
        val f = fixture()
        val vm = ProfileViewModel(f.project, f.user)

        vm.trySendAction(ProfileAction.SignOutRequested)
        assertTrue(vm.stateFlow.value.confirmingSignOut)

        vm.trySendAction(ProfileAction.SignOutCancelled)
        assertFalse(vm.stateFlow.value.confirmingSignOut)
    }

    @Test
    fun confirmingSignOutClearsBOTHHalvesOfTheSession() = runTest {
        // THE one that matters. `RootNavViewModel` gates on `isAuthenticated`, which lives on the
        // framework's user prefs — clearing only the Fineract half would leave the shell believing
        // the user is still signed in, and the app would return to the dashboard on next launch.
        val f = fixture()
        val vm = ProfileViewModel(f.project, f.user)

        vm.eventFlow.test {
            vm.trySendAction(ProfileAction.SignOutConfirmed)
            assertEquals(ProfileEvent.SignedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(
            f.project.fineractUser.value.base64EncodedAuthenticationKey.isNullOrBlank(),
            "the Fineract session key survived sign-out",
        )
        assertFalse(
            f.user.userData.value.isAuthenticated,
            "the framework auth flag survived sign-out — the shell would still think we are in",
        )
    }

    @Test
    fun signOutNeedsNoNetwork() = runTest {
        // Deliberately NOT a MutationGateway mutation (MIGRATION.md §4): a field officer who
        // cannot sign out without a connection is stranded. There is no repository to be offline
        // here, and this test exists so that stays true — anyone wiring a network call into
        // `signOut()` has to delete it.
        val f = fixture()
        val vm = ProfileViewModel(f.project, f.user)

        vm.eventFlow.test {
            vm.trySendAction(ProfileAction.SignOutConfirmed)
            // Completes with no network and no dispatcher advancing beyond Unconfined.
            assertEquals(ProfileEvent.SignedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(vm.stateFlow.value.confirmingSignOut)
    }
}
