/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.ui

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the field-officer [HomeViewModel] contract:
 *
 *  - Initial state exposes the full field-officer feature set, in order.
 *  - Every [HomeAction.FeatureClicked] reduces to exactly one [HomeEvent.NavigateTo] carrying the
 *    same target — the tap → navigation-intent path the home board relies on.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @BeforeTest
    fun setUp() {
        // Unconfined so the BaseViewModel action-processing coroutine (launched in viewModelScope,
        // which is Dispatchers.Main) runs eagerly and events surface without manual advancing.
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateExposesEveryFieldOfficerFeatureInOrder() = runTest {
        val vm = HomeViewModel()

        val features = vm.stateFlow.first().features

        assertEquals(
            listOf(
                HomeFeature.CLIENTS,
                HomeFeature.GROUPS,
                HomeFeature.CENTERS,
                HomeFeature.LOANS,
                HomeFeature.COLLECTION_SHEET,
                HomeFeature.CHECKER_INBOX,
                HomeFeature.PATH_TRACKING,
                HomeFeature.SEARCH,
            ),
            features,
            "Home board must offer the full field-officer feature set.",
        )
    }

    @Test
    fun clientsTileClickEmitsNavigateToClients() = runTest {
        val vm = HomeViewModel()

        vm.eventFlow.test {
            vm.trySendAction(HomeAction.FeatureClicked(HomeFeature.CLIENTS))
            assertEquals(HomeEvent.NavigateTo(HomeFeature.CLIENTS), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun everyFeatureClickEmitsMatchingNavigateEvent() = runTest {
        val vm = HomeViewModel()

        vm.eventFlow.test {
            HomeFeature.entries.forEach { feature ->
                vm.trySendAction(HomeAction.FeatureClicked(feature))
                assertEquals(
                    HomeEvent.NavigateTo(feature),
                    awaitItem(),
                    "Tapping $feature must navigate to $feature.",
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
