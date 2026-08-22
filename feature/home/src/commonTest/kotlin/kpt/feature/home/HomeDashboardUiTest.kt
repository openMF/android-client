/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kpt.feature.home.ui.HomeFeature
import kpt.feature.home.ui.HomeViewModel
import kpt.feature.home.ui.TestTags
import kotlin.test.Test

/**
 * Compose UI test for the field-officer [HomeDashboard] body. Renders the board with a
 * plain-constructed [HomeViewModel] (no Koin) and asserts the scroll container plus a
 * representative tile from each band render — proving the real officer entry points are on screen
 * rather than the removed currency/macro demo dashboard (RULE-KMP-COMPOSE-UITEST-001).
 */
@OptIn(ExperimentalTestApi::class)
class HomeDashboardUiTest {

    @Test
    fun officerEntryTilesAreDisplayed() = runComposeUiTest {
        setContent {
            KptTheme {
                HomeDashboard(
                    onNavigate = {},
                    viewModel = HomeViewModel(),
                )
            }
        }

        onNodeWithTag(TestTags.Home.DASHBOARD_SCROLL).assertIsDisplayed()
        // Top management tile is on screen.
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.CLIENTS)).assertIsDisplayed()
        // Remaining tiles are composed in the scroll container (may be below the fold).
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.GROUPS)).assertExists()
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.CENTERS)).assertExists()
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.COLLECTION_SHEET)).assertExists()
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.CHECKER_INBOX)).assertExists()
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.PATH_TRACKING)).assertExists()
        onNodeWithTag(TestTags.Home.featureTile(HomeFeature.SEARCH)).assertExists()
    }
}
