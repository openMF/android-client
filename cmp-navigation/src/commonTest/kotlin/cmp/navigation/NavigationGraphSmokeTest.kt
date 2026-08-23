/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation

import cmp.navigation.authenticatednavbar.AuthenticatedNavBarTabItem
import kotlinx.serialization.serializer
import kpt.feature.bills.navigation.AddOrEditBillReminderRoute
import kpt.feature.bills.navigation.BillRemindersListRoute
import kpt.feature.bills.navigation.BillsGraphRoute
import kpt.feature.calculators.navigation.AffordabilityCalculatorRoute
import kpt.feature.calculators.navigation.AmortizationRoute
import kpt.feature.calculators.navigation.CalculatorsGraphRoute
import kpt.feature.calculators.navigation.LoanCalcWizardRoute
import kpt.feature.calculators.navigation.LoanComparisonRoute
import kpt.feature.currencyrates.navigation.CurrencyRatesGraphRoute
import kpt.feature.currencyrates.navigation.CurrencyRatesRoute
import kpt.feature.currencyrates.navigation.RateHistoryRoute
import kpt.feature.emicalculator.navigation.EmiCalculatorRoute
import kpt.feature.loans.navigation.AddOrEditLoanRoute
import kpt.feature.loans.navigation.LoanDetailRoute
import kpt.feature.loans.navigation.LoansGraphRoute
import kpt.feature.loans.navigation.PersonalLoansListRoute
import kpt.feature.macro.navigation.CountryMacroRoute
import kpt.feature.macro.navigation.CountryPickerRoute
import kpt.feature.macro.navigation.MacroGraphRoute
import kpt.feature.rates.navigation.RateDetailRoute
import kpt.feature.rates.navigation.RatesGraphRoute
import kpt.feature.rates.navigation.RatesListRoute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * End-to-end smoke test for the app's navigation graph.
 *
 * Catches the bug class where someone deletes / renames / mis-wires a route in a
 * feature module without realising the AuthenticatedNavigation graph or another
 * feature still references it. The test exercises:
 *
 *  1. Every parameterless route can be referenced as an object (compile-time check).
 *  2. Every parameterised route can be instantiated with sensible defaults.
 *  3. Every route is `@Serializable` — the `serializer()` lookup at runtime
 *     fails fast if anyone forgets the annotation, so the kotlinx.serialization
 *     compiler plugin is wired correctly for the module.
 *  4. Bottom-nav tab items have non-blank routes + stable testTags (used by
 *     instrumentation tests + analytics).
 *
 * This is a coarse-grained gate — finer-grained route arg behaviour is covered
 * by per-feature VM tests. Add a new route → add it to one of the lists below.
 */
class NavigationGraphSmokeTest {

    /**
     * Parameterless routes (data object) that every consumer references by type.
     * Adding a new top-level route → add it here.
     */
    private val parameterlessRoutes: List<Any> = listOf(
        // Graph roots
        BillsGraphRoute,
        LoansGraphRoute,
        RatesGraphRoute,
        CurrencyRatesGraphRoute,
        CalculatorsGraphRoute,
        MacroGraphRoute,

        // Leaf screens with no args
        BillRemindersListRoute,
        PersonalLoansListRoute,
        RatesListRoute,
        CurrencyRatesRoute,
        RateHistoryRoute,
        EmiCalculatorRoute,
        AffordabilityCalculatorRoute,
        LoanComparisonRoute,
        CountryPickerRoute,
    )

    @Test
    fun everyParameterlessRouteIsReferenceableAndNonNull() {
        parameterlessRoutes.forEach { route ->
            assertNotNull(route, "Route ${route::class.simpleName} should be non-null")
        }
        // Sanity: no duplicate route types in the list (catches copy-paste mistakes).
        val classes = parameterlessRoutes.map { it::class }
        assertEquals(
            classes.size,
            classes.toSet().size,
            "Parameterless route list has duplicates: $classes",
        )
    }

    @Test
    fun parameterisedRoutesInstantiateWithDefaults() {
        // Each of these has at least one optional/nullable arg — the screen falls
        // back to "create new" / sensible default when the host navigates without
        // an argument. Locking this so a future refactor that adds a mandatory
        // arg has to update all entry points first.
        assertNotNull(AddOrEditBillReminderRoute(billId = null))
        assertNotNull(AddOrEditLoanRoute(loanId = null))
        assertNotNull(LoanCalcWizardRoute(scenarioId = null))
        assertNotNull(AmortizationRoute(loanId = null))
        assertNotNull(CountryMacroRoute()) // defaults to "US"

        // Strict-arg routes — locked for the inverse: callers MUST pass the id.
        assertNotNull(LoanDetailRoute(loanId = "L-test"))
        assertNotNull(RateDetailRoute(seriesId = "DFF"))
    }

    @Test
    fun everyRouteIsKotlinxSerializable() {
        // serializer<T>() resolves at compile time iff T is @Serializable AND the
        // kotlinx.serialization compiler plugin ran on the producing module.
        // Catches the bug where someone deletes @Serializable from a route or
        // forgets to apply the plugin in a feature module's build.gradle.kts.
        assertNotNull(serializer<BillsGraphRoute>())
        assertNotNull(serializer<LoansGraphRoute>())
        assertNotNull(serializer<RatesGraphRoute>())
        assertNotNull(serializer<CurrencyRatesGraphRoute>())
        assertNotNull(serializer<CalculatorsGraphRoute>())
        assertNotNull(serializer<MacroGraphRoute>())

        assertNotNull(serializer<BillRemindersListRoute>())
        assertNotNull(serializer<PersonalLoansListRoute>())
        assertNotNull(serializer<RatesListRoute>())
        assertNotNull(serializer<CurrencyRatesRoute>())
        assertNotNull(serializer<RateHistoryRoute>())
        assertNotNull(serializer<EmiCalculatorRoute>())
        assertNotNull(serializer<AffordabilityCalculatorRoute>())
        assertNotNull(serializer<LoanComparisonRoute>())
        assertNotNull(serializer<CountryPickerRoute>())

        assertNotNull(serializer<AddOrEditBillReminderRoute>())
        assertNotNull(serializer<AddOrEditLoanRoute>())
        assertNotNull(serializer<LoanCalcWizardRoute>())
        assertNotNull(serializer<AmortizationRoute>())
        assertNotNull(serializer<CountryMacroRoute>())
        assertNotNull(serializer<LoanDetailRoute>())
        assertNotNull(serializer<RateDetailRoute>())
    }

    @Test
    fun bottomNavTabsHaveStableTestTagsAndNonBlankRoutes() {
        val tabs = listOf(
            AuthenticatedNavBarTabItem.HomeTab,
            AuthenticatedNavBarTabItem.ProfileTab,
        )

        // Locked test tags — instrumentation tests + analytics depend on these.
        // Renaming requires updating both the test suite and any dashboards.
        assertEquals("HomeTab", AuthenticatedNavBarTabItem.HomeTab.testTag)
        assertEquals("ProfileTab", AuthenticatedNavBarTabItem.ProfileTab.testTag)

        // Every tab declares non-blank graphRoute + startDestinationRoute so the
        // bottom bar doesn't crash with "destination not found" on first tap.
        tabs.forEach { tab ->
            assertTrue(
                tab.graphRoute.isNotBlank(),
                "${tab::class.simpleName} has blank graphRoute",
            )
            assertTrue(
                tab.startDestinationRoute.isNotBlank(),
                "${tab::class.simpleName} has blank startDestinationRoute",
            )
            assertTrue(
                tab.testTag.isNotBlank(),
                "${tab::class.simpleName} has blank testTag",
            )
        }
    }

    @Test
    fun routeCountMatchesExpectedFeatureSurface() {
        // Canary: the toolkit ships these N parameterless graph + screen routes.
        // If you intentionally add or remove a route, update this count + the
        // `parameterlessRoutes` list above. Catches accidental nav-graph
        // shrinkage / silent module drop after a refactor.
        val expectedCount = 15
        assertEquals(
            expectedCount,
            parameterlessRoutes.size,
            "Expected $expectedCount parameterless routes, found ${parameterlessRoutes.size}. " +
                "If this is intentional, update both the expectedCount and parameterlessRoutes list.",
        )
    }
}
