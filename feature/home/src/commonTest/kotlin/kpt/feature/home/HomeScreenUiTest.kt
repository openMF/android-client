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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kpt.feature.home.ui.HomeViewModel
import kpt.feature.home.ui.TestTags
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for [HomeScreen] — the top-level bottom-nav shell that wraps the
 * fork-owned home body inside a framework-owned [androidx.compose.material3.Scaffold] with a
 * settings top-bar action.
 *
 * Renders the real field-officer [HomeDashboard] as the body (mirroring production, where
 * `cmp-navigation`'s `BackboneRegistry.homeBody` supplies it) with a plain-constructed
 * [HomeViewModel] (no Koin — the officer home VM has no constructor dependencies) and asserts
 * [TestTags.Home.SCREEN], the root Scaffold testTag, is always displayed (RULE-KMP-COMPOSE-UITEST-001
 * CU-1..CU-3).
 */
@OptIn(ExperimentalTestApi::class)
class HomeScreenUiTest {

    @Test
    fun screenScaffoldIsDisplayed() = runComposeUiTest {
        setContent {
            KptTheme {
                HomeScreen(
                    onSettingsClick = {},
                    homeBody = {
                        HomeDashboard(
                            onNavigate = {},
                            viewModel = HomeViewModel(),
                        )
                    },
                )
            }
        }
        onNodeWithTag(TestTags.Home.SCREEN).assertIsDisplayed()
    }
}
