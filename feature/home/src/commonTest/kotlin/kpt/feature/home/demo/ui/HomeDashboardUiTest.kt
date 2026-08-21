/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.demo.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kpt.feature.home.demo.HomeDashboard
import kpt.feature.home.ui.TestTags
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for the multi-source [HomeDashboard] — the "hard case"
 * (a 4-repository dashboard `internal` composable) proving the commonTest harness scales
 * beyond a single-repo screen. Reuses the fake repositories already defined in
 * [HomeDashboardViewModelTest] (widened to `internal`) to build the real [HomeViewModel]
 * with no Koin, wraps in [KptTheme], and asserts the dashboard scroll container renders.
 */
@OptIn(ExperimentalTestApi::class)
class HomeDashboardUiTest {

    @Test
    fun dashboardScrollIsDisplayed() = runComposeUiTest {
        val viewModel = HomeViewModel(
            loanRepository = EmptyLoanRepository,
            billReminderRepository = EmptyBillReminderRepository,
            economicRatesRepository = LoadingEconomicRatesRepository,
            currencyRepository = FakeDashboardCurrencyRepository(),
        )
        setContent {
            KptTheme {
                HomeDashboard(
                    onNavigateToLoans = {},
                    onNavigateToBills = {},
                    onNavigateToRates = {},
                    onNavigateToExchangeRates = {},
                    onNavigateToRateHistory = {},
                    onNavigateToMacro = {},
                    onNavigateToEmi = {},
                    onNavigateToAffordability = {},
                    onNavigateToAmortization = {},
                    onNavigateToLoanComparison = {},
                    onNavigateToLoanCalcWizard = {},
                    onNavigateToCrypto = {},
                    viewModel = viewModel,
                )
            }
        }
        onNodeWithTag(TestTags.Home.DASHBOARD_SCROLL).assertIsDisplayed()
    }
}
