/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(kpt.core.data.testing.ExperimentalDataTestingApi::class)

package kpt.feature.loan.charges

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kpt.core.base.store.screen.ScreenState
import kpt.core.data.testing.FakeLoanRepository
import kpt.core.database.charge.entity.ChargesEntity
import kpt.core.database.charge.entity.ClientChargeCurrencyEntity
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LoanChargesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private val chargesState =
        MutableStateFlow<ScreenState<List<ChargesEntity>>>(ScreenState.Loading)
    private val repository = FakeLoanRepository(loanCharges = chargesState)

    private fun viewModel(loanId: Int = 7) =
        LoanChargesViewModel(repository, SavedStateHandle(mapOf("loanId" to loanId)))

    private fun charge(
        id: Int,
        chargeId: Int? = 11,
        name: String? = "Processing Fee",
        amount: Double? = 1_500.0,
        dueDate: List<Int>? = listOf(2026, 3, 14),
    ) = ChargesEntity(
        id = id,
        loanId = 7,
        chargeId = chargeId,
        name = name,
        amount = amount,
        dueDate = dueDate,
        currency = ClientChargeCurrencyEntity(code = "INR", decimalPlaces = 2),
    )

    private suspend fun rows(vm: LoanChargesViewModel): List<LoanChargeRow> {
        var result: List<LoanChargeRow>? = null
        vm.charges.test {
            assertIs<ScreenState.Loading>(awaitItem())
            result = assertIs<ScreenState.Content<List<LoanChargeRow>>>(awaitItem()).data
            cancelAndIgnoreRemainingEvents()
        }
        return assertNotNull(result)
    }

    @Test
    fun theRouteArgumentReachesTheRead() = runTest(dispatcher) {
        viewModel(loanId = 42)
        testScheduler.advanceUntilIdle()
        assertEquals(listOf<Any>(42), repository.requestedKeys["loanCharges"]?.toList())
    }

    @Test
    fun aChargesAmountIsFormattedWithItsOwnCurrency() = runTest(dispatcher) {
        // The original printed `amount.toString()`, so a 1500.0 charge read "1500.0" — no symbol,
        // no grouping, and nothing to tell two currencies apart.
        val vm = viewModel()
        chargesState.value = ScreenState.Content(listOf(charge(1)))

        val row = rows(vm).single()

        assertTrue(row.amount.contains("1,500"), "amount was ${row.amount}")
    }

    @Test
    fun aNullAmountOrChargeIdRendersAPlaceholderNotTheWordNull() = runTest(dispatcher) {
        // Both were `toString()`-ed straight off a nullable field.
        val vm = viewModel()
        chargesState.value = ScreenState.Content(
            listOf(charge(1, chargeId = null, amount = null, name = null)),
        )

        val row = rows(vm).single()

        assertEquals("—", row.amount)
        assertEquals("—", row.chargeId)
        assertEquals("", row.name)
    }

    @Test
    fun theDueDateUsesTheAppsDisplayFormat() = runTest(dispatcher) {
        // `ChargesEntity.formattedDueDate` builds "${d[0]}-${d[1]}-${d[2]}", so a March charge
        // read "2026-3-14" — year-first, unpadded, and unlike every other date in the app.
        val vm = viewModel()
        chargesState.value = ScreenState.Content(listOf(charge(1)))

        assertEquals("14 Mar 2026", rows(vm).single().dueDate)
    }

    @Test
    fun aChargeWithNoUsableDueDateRendersAPlaceholder() = runTest(dispatcher) {
        // The entity's own fallback is the hardcoded English literal "No Due Date"; this uses a
        // dash and lets the label say what it is.
        val vm = viewModel()
        chargesState.value = ScreenState.Content(
            // A partial date — `getDateAsString` indexes `[2]` directly, so this would throw
            // unguarded.
            listOf(charge(1, dueDate = null), charge(2, dueDate = listOf(2026))),
        )

        assertEquals(listOf("—", "—"), rows(vm).map { it.dueDate })
    }

    @Test
    fun aLoanWithNoChargesIsEmptyNotAnEmptyList() = runTest(dispatcher) {
        val vm = viewModel()
        chargesState.value = ScreenState.Content(emptyList())

        vm.charges.test {
            assertIs<ScreenState.Loading>(awaitItem())
            assertIs<ScreenState.Empty>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun theRowKeyIsTheLEVIEDChargesIdNotTheDefinitionId() = runTest(dispatcher) {
        // `chargeId` is the charge-DEFINITION id and repeats across every loan carrying that
        // charge; `id` is this levied instance. Keying the list on `chargeId` would collide for a
        // loan charged the same fee twice.
        val vm = viewModel()
        chargesState.value = ScreenState.Content(
            listOf(charge(1, chargeId = 11), charge(2, chargeId = 11)),
        )

        val result = rows(vm)

        assertEquals(listOf(1, 2), result.map { it.id })
        assertEquals(listOf("11", "11"), result.map { it.chargeId })
    }

    @Test
    fun refreshAndRetryBothReachTheRead() = runTest(dispatcher) {
        val vm = viewModel()
        chargesState.value = ScreenState.Content(listOf(charge(1)))
        testScheduler.advanceUntilIdle()

        repository.refreshes.test {
            vm.actionChannel.trySend(LoanChargesAction.OnRefresh)
            testScheduler.advanceUntilIdle()
            awaitItem()
            // ONE member for both the pull gesture and the error-state retry, because
            // `RefreshableScreenContent` routes both to the same callback — and because
            // `ScreenDataStream.retry()` is literally `refresh(forceFresh = true)`. The original
            // owned an `isRefreshing` boolean it set true -> false around a call that only
            // LAUNCHED a coroutine, so its spinner could never be observed true; the stream owns
            // that signal now.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun addChargeCarriesTheLoanId() = runTest(dispatcher) {
        val vm = viewModel(loanId = 7)
        vm.eventFlow.test {
            vm.actionChannel.trySend(LoanChargesAction.OnAddCharge)
            assertEquals(LoanChargesEvent.AddCharge(loanId = 7), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun backNavigationIsAnEvent() = runTest(dispatcher) {
        val vm = viewModel()
        vm.eventFlow.test {
            vm.actionChannel.trySend(LoanChargesAction.NavigateBack)
            assertEquals(LoanChargesEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
